package uk.ac.westminster.productsapi;

public class PhysicalProduct extends Product implements Discountable {
    private double discountedPrice;
    private double weightKg;
    private String dimensions;

    public PhysicalProduct(){
    }
    public PhysicalProduct(Long id, String name, double price, double weight, String dimensions){
        super(id, name, price);
        this.weightKg = weight;
        this.dimensions = dimensions;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public String getDimensions() {
        return dimensions;
    }

    @Override
    public String getSummary() {
        return "Physical" + getName() + " |weight-"+ weightKg;
    }

    @Override
    public void applyDiscount(double rate) {
        double capped = Math.min(rate, 0.20);
        this.discountedPrice = getPrice()*(1-capped);

    }

    @Override
    public double getDiscountedPrice() {
        return discountedPrice;
    }
}
