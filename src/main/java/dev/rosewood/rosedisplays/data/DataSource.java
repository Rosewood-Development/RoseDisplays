package dev.rosewood.rosedisplays.data;

public interface DataSource {

    /**
     * Loads the data, this will be run async
     *
     * @param targetWidth The desired width of the data
     * @param targetHeight The desired height of the data
     */
    void loadData(int targetWidth, int targetHeight) throws Exception;

    /**
     * @return the maximum width of the data
     */
    int getWidth();

    /**
     * @return the maximum height of the data
     */
    int getHeight();

    /**
     * @return the color data for the next frame
     */
    int[] nextFrame();

    /**
     * @return true if the data source has multiple frames, otherwise false
     */
    boolean isStatic();

    /**
     * @return the delay of the next frame
     */
    int getFrameDelay();

    /**
     * @return the name of the data source
     */
    String getName();

}
