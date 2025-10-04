// Coffee order class - represents what customers order
class CoffeeOrder {
    private String customerName;
    private String coffeeType;
    
    public CoffeeOrder(String customerName, String coffeeType) {
        this.customerName = customerName;
        this.coffeeType = coffeeType;
    }
    
    public String getCustomerName() { return customerName; }
    public String getCoffeeType() { return coffeeType; }
    
    @Override
    public String toString() {
        return coffeeType + " for " + customerName;
    }
}

// Barista class - each barista is a separate thread
class Barista implements Runnable {
    private String baristaName;
    private CoffeeOrder[] orders;
    
    public Barista(String name, CoffeeOrder[] orders) {
        this.baristaName = name;
        this.orders = orders;
    }
    
    @Override
    public void run() {
        System.out.println("☕ " + baristaName + " started working!");
        
        for (CoffeeOrder order : orders) {
            System.out.println("👨‍🍳 " + baristaName + " is making " + order);
            
            // Simulate time to make coffee (1-3 seconds)
            try {
                int makeTime = 1000 + (int)(Math.random() * 2000);
                Thread.sleep(makeTime);
            } catch (InterruptedException e) {
                System.out.println("❌ " + baristaName + " was interrupted!");
                return;
            }
            
            System.out.println("✅ " + baristaName + " finished " + order);
        }
        
        System.out.println("🏁 " + baristaName + " finished all orders!");
    }
}

// Shared tip jar that all baristas contribute to
class TipJar {
    private double totalTips = 0.0;
    
    // Synchronized method to prevent money counting errors
    public synchronized void addTip(double amount, String baristaName) {
        totalTips += amount;
        System.out.println("💰 " + baristaName + " added $" + amount + 
                         " to tips. Total: $" + totalTips);
    }
    
    public synchronized double getTotalTips() {
        return totalTips;
    }
}

// Barista with tips - shows synchronization
class TippedBarista implements Runnable {
    private String baristaName;
    private TipJar tipJar;
    private int ordersToMake;
    
    public TippedBarista(String name, TipJar tipJar, int orders) {
        this.baristaName = name;
        this.tipJar = tipJar;
        this.ordersToMake = orders;
    }
    
    @Override
    public void run() {
        System.out.println("☕ " + baristaName + " started working for tips!");
        
        for (int i = 1; i <= ordersToMake; i++) {
            System.out.println("👨‍🍳 " + baristaName + " making order #" + i);
            
            try {
                Thread.sleep(800); // Time to make coffee
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            
            // Add random tip ($1-5)
            double tip = 1.0 + Math.random() * 4.0;
            tipJar.addTip(Math.round(tip * 100.0) / 100.0, baristaName);
        }
        
        System.out.println("🏁 " + baristaName + " finished working!");
    }
}

// Main class
public class coffeeShopSimulation {
    
    public static void main(String[] args) {
        System.out.println("☕ WELCOME TO JAVA COFFEE SHOP! ☕");
        System.out.println("Learning multithreading with coffee!\n");
        
        // Run both examples
        runBasicExample();
        System.out.println("\n" + "=".repeat(50) + "\n");
        runSynchronizationExample();
        
        System.out.println("\n🎉 Thanks for visiting Java Coffee Shop!");
        System.out.println("You learned: Thread creation, parallel execution, and synchronization!");
    }
    
    private static void runBasicExample() {
        System.out.println("📋 EXAMPLE 1: BASIC MULTITHREADING");
        System.out.println("Two baristas working on different orders simultaneously");
        System.out.println("-".repeat(50));
        
        // Create coffee orders
        CoffeeOrder[] orders1 = {
            new CoffeeOrder("Alice", "Cappuccino"),
            new CoffeeOrder("Bob", "Latte")
        };
        
        CoffeeOrder[] orders2 = {
            new CoffeeOrder("Charlie", "Espresso"),
            new CoffeeOrder("Diana", "Mocha")
        };
        
        // Create barista threads
        Thread barista1 = new Thread(new Barista("Emma", orders1));
        Thread barista2 = new Thread(new Barista("Jack", orders2));
        
        // Record start time
        long startTime = System.currentTimeMillis();
        
        // Start both baristas working
        barista1.start();
        barista2.start();
        
        try {
            // Wait for both to finish
            barista1.join();
            barista2.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted!");
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("\n⏱️  Total time: " + (totalTime/1000.0) + " seconds");
        System.out.println("💡 Notice: Both baristas worked at the same time!");
        System.out.println("   Without multithreading, this would take much longer!");
    }
    
    private static void runSynchronizationExample() {
        System.out.println("📋 EXAMPLE 2: THREAD SYNCHRONIZATION");
        System.out.println("Three baristas sharing one tip jar (needs synchronization!)");
        System.out.println("-".repeat(50));
        
        // Shared tip jar
        TipJar sharedTipJar = new TipJar();
        
        // Create three baristas sharing the tip jar
        Thread barista1 = new Thread(new TippedBarista("Emma", sharedTipJar, 3));
        Thread barista2 = new Thread(new TippedBarista("Jack", sharedTipJar, 3));
        Thread barista3 = new Thread(new TippedBarista("Sofia", sharedTipJar, 3));
        
        // Start all baristas
        barista1.start();
        barista2.start();
        barista3.start();
        
        try {
            // Wait for all to finish
            barista1.join();
            barista2.join();
            barista3.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted!");
        }
        
        System.out.println("\n💰 FINAL RESULT:");
        System.out.println("Total tips collected: $" + sharedTipJar.getTotalTips());
        System.out.println("💡 Notice: No money was lost even with multiple baristas!");
        System.out.println("   The 'synchronized' keyword prevented counting errors!");
    }
}

