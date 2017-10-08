/**
 *
 * AzureComputerVisionProcessor is used to query the Azure Cognitive Computer Vision API.
 * It can directly upload image as binary as application/octet-stream.
 *
 * The result from Azure Cognitive Service is also process in this class and could be used
 * to enhance the feature of our SEDA app.
 *
 * This class is built based on the Azure Cognitive Computer Vision sample code
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */

package seda.baseapp.Azure;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AzureComputerVisionProcessor
{

    // **********************************************
    // *** Update or verify the following values. ***
    // **********************************************

    // Replace the subscriptionKey string value with your valid subscription key.
//    public static final String subscriptionKey = "13hc77781f7e4b19b5fcdd72a8df7156";

    // Replace or verify the region.
    //
    // You must use the same region in your REST API call as you used to obtain your subscription keys.
    // For example, if you obtained your subscription keys from the westus region, replace
    // "westcentralus" in the URI below with "westus".
    //
    // NOTE: Free trial subscription keys are generated in the westcentralus region, so if you are using
    // a free trial subscription key, you should not need to change this region.
//    public static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";
    public static final String uriBase = "https://australiaeast.api.cognitive.microsoft.com/vision/v1.0/analyze?visualFeatures=Tags&language=en";

    public static final String subKey = "e92ff2fa7a934f68807e1cca26b97b62";

    String attachmentName = "bitmap";
    String attachmentFileName = "bitmap.bmp";
    String crlf = "\r\n";
    String twoHyphens = "--";
    String boundary =  "*****";


    private Context context;

    AzureComputerVisionProcessor(Context context)
    {
           this.context = context;
    }

    /**
     * This method is used to query the Azure Cognitive Computer Vision API and get back
     * the result of the image analysis to see what is inside the photo by examing the
     * confidence value.
     *
     * The query code is inspired from:
     * https://stackoverflow.com/questions/34295443/applilation-octet-stream-android
     * @return void
     */
    public double getConfidenceValueFromAzureVisionAPI(String imageFileName, String tagName)
    {

        int BUFFER_SIZE = 4096;
        String method = "POST";;
//        String filePath = "photo.JPEG";
        String filePath = imageFileName + ".jpg";

        File uploadFile = new File(filePath);


        try
        {
            URL url = new URL(uriBase);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            String contentType = "application/octet-stream";

            httpConn.setDoOutput(true);
            httpConn.setRequestMethod(method);
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Content-type", contentType);
            httpConn.setRequestProperty("Ocp-Apim-Subscription-Key", subKey);
            OutputStream outputStream = httpConn.getOutputStream();
            BufferedReader br;

            FileInputStream inputStream = new FileInputStream( "photo.png");
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            br = new BufferedReader(new InputStreamReader((httpConn.getInputStream())));

            Log.d("response", "" + httpConn.getResponseMessage());
            Log.d("response", "" + httpConn.getContent());

            String line = null;
            String responseLine ="";
            while((line=br.readLine()) != null)
            {
                responseLine += line;
                Log.d("response line", line);
            }
            JSONObject jsonObject = new JSONObject(responseLine);
            JSONArray jsonArray = jsonObject.getJSONArray("tags");

            for(int i = 0; i < jsonArray.length(); i++)
            {
                String name = jsonArray.getJSONObject(i).getString("name");
                double confidence = jsonArray.getJSONObject(i).getDouble("confidence");
                Log.d("name and confidence", "name -> " + name +", confidence -> " + confidence);
                if(name.equalsIgnoreCase(tagName))
                {
                    return confidence;
                }

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;



//                    https://stackoverflow.com/questions/4989182/converting-java-bitmap-to-byte-array
//            File imageFile = CameraActivity.getOutputMediaFile(CameraActivity.MEDIA_TYPE_IMAGE);
//            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


    }

}
