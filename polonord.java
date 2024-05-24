import java.util.concurrent.Semaphore;

class polonord {
    private static final int numRenne = 9;
    private static final int numElfi = 5;

    private int renneCount = 0;
    private int elfiCount = 0;
    private final Semaphore babboNataleSem = new Semaphore(0);
    private final Semaphore renneSem = new Semaphore(0);
    private final Semaphore elfiSem = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore elfiMutex = new Semaphore(1);

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

    class BabboNatale implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    babboNataleSem.acquire();
                    mutex.acquire();
                    try {
                        if (renneCount == numRenne) {
                            if (elfiCount == 3) {
                                System.out.println("Babbo Natale decide di aiutare le renne invece degli elfi."); //boh
                            }
                            preparaSlitta();
                            consegnaRegali();
                            renneSem.release(numRenne);
                            renneCount = 0;
                        } else if (elfiCount == 3) {
                            aiutaElfi();
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
            System.out.println("Preparazione della slitta...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void consegnaRegali() {
            System.out.println("Babbo Natale sta consegnando i regali...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Babbo Natale è tornato.");
        }

        private void aiutaElfi() {
            System.out.println("Babbo Natale sta aiutando gli elfi!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                    Thread.sleep((int) (Math.random() * 5000));
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
                    rennaAspetta();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void rennaAspetta() {
            System.out.println("La renna " + id + " sta aspettando"); //boh
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Elfo implements Runnable {
        private final int id;

        Elfo(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    costruisceGiocattolo();
                    elfiMutex.acquire();
                    mutex.acquire();
                    try {
                        elfiCount++;
                        if (elfiCount == 3) {
                            babboNataleSem.release();
                        } else {
                            elfiMutex.release();
                        }
                    } finally {
                        mutex.release();
                    }
                    elfiSem.acquire();
                    aiuto();
                    mutex.acquire();
                    try {
                        elfiCount--;
                        if (elfiCount == 0) {
                            elfiMutex.release();
                        }
                    } finally {
                        mutex.release();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void costruisceGiocattolo() {
            System.out.println("L'elfo " + id + " sta costruendo un giocattolo.");
            try {
                Thread.sleep((int) (Math.random() * 3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void aiuto() {
            System.out.println("L'elfo " + id + " sta ricevendo aiuto da Babbo Natale.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
