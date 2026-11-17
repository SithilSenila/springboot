package uk.ac.westminster.productsapi;

public class DigitalProduct extends Product{
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
}
