package uk.ac.westminster.productsapi;

public class DigitalProduct extends Product implements Discountable{
    private double discountedPrice;
    private String downloadUrl;
    private double fileSizeMB;


    public DigitalProduct(Long id, String  name ,double price, String downloadUrl, double fileSzieMB){
        super(id, name , price);
        this.downloadUrl = downloadUrl;
        this.fileSizeMB = fileSzieMB;
    }

    public String getDownloadUrl(){
        return downloadUrl;
    }

    public double getFileSize(){
        return fileSizeMB;
    }

    @Override
    public String getSummary(){
        return "Digital" + getName() + "|Download"+  downloadUrl;
    }


    @Override
    public void applyDiscount(double rate){
        this.discountedPrice =  getPrice() * (1-rate);
    }
    @Override
    public double getDiscountedPrice(){
        return discountedPrice;
    }

}
