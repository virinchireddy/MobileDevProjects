package sooner.om.com.sooner.helper;

/**
 * Created by SaiKrupa on 10/26/2016.
 */

public class InsuranceCard {
    private String[] images;

    public InsuranceCard(String[] images) {
        this.images = images;
    }

    public String getImageId() {

        int imagesId = 0;
        return images[imagesId];
    }

    public String getImageUrl() {

        int imageUrl = 1;
        return images[imageUrl];
    }

}
