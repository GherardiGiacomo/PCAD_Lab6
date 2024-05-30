import java.util.concurrent.Semaphore;
import java.util.Random;

class polonord {
    private static final int numRenne = 9;
    private static final int numElfi = 4;

    private int renneCount = 0;
    private int elfiCount = 0;
    private final Semaphore babboNataleSem = new Semaphore(0);
    private final Semaphore renneSem = new Semaphore(0);
    private final Semaphore elfiSem = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore elfiMutex = new Semaphore(1);
    private final Semaphore babboNataleRitornoSem = new Semaphore(numElfi); // Nuovo semaforo

    public static void main(String[] args) {
        polonord problem = new polonord();
        problem.start();
    }

    public void start() {
        new Thread(new BabboNatale()).start();

        for (int i = 0; i < numRenne; i++) {
            new Thread(new Renna(i)).start();
        }

        for (int i = 0; i < numElfi; i++) {
            new Thread(new Elfo(i)).start();
        }
    }

    class Elfo implements Runnable {
        private final int id;
        private final Random random = new Random();

        Elfo(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    babboNataleRitornoSem.acquire(); // Aggiunto qui
                    System.out.println("L'elfo " + id + " sta cercando di costruire un giocattolo.");
                    Thread.sleep(2000);
                    if (random.nextInt(10) < 3) { // 30% di probabilità che l'elfo non riesca a costruire un giocattolo
                        System.out.println("L'elfo " + id + " non è riuscito a costruire un giocattolo e ha bisogno di aiuto da Babbo Natale.");
                        elfiMutex.acquire();
                        try {
                            elfiCount++;
                            if (elfiCount == 3) {
                                babboNataleSem.release();
                            } else {
                                elfiMutex.release();
                            }
                        } finally {
                            if (elfiCount == 3) {
                                elfiSem.acquire(3);
                                elfiMutex.release();
                            }
                        }
                        System.out.println("L'elfo " + id + " sta ricevendo aiuto da Babbo Natale.");
                        Thread.sleep(1000);
                    } else {
                        System.out.println("L'elfo " + id + " è riuscito a costruire un giocattolo.");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class BabboNatale implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    babboNataleSem.acquire();
                    mutex.acquire();
                    try {
                        if (renneCount == numRenne) {
                            preparaSlitta();
                            consegnaRegali();
                            renneSem.release(numRenne);
                            renneCount = 0;
                            babboNataleRitornoSem.release(numElfi);
                        } else if (elfiCount == 3) {
                            //aiutaElfi();
                            elfiSem.release(3);
                            elfiCount = 0;
                        }
                    } finally {
                        mutex.release();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void preparaSlitta() {
            System.out.println("\nPreparazione della slitta...");
        }

        private void consegnaRegali() {
            System.out.println("Babbo Natale sta consegnando i regali...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Babbo Natale è tornato.\n");
        }

        private void aiutaElfi() {
            System.out.println("Babbo Natale sta aiutando gli elfi!\n");
        }
    }

    class Renna implements Runnable {
        private final int id;

        Renna(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(10000);
                    mutex.acquire();
                    try {
                        renneCount++;
                        if (renneCount == numRenne) {
                            babboNataleSem.release();
                        }
                    } finally {
                        mutex.release();
                    }
                    renneSem.acquire();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}