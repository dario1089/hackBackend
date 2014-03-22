package hack;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class IPoolAPI {

//	public static void main(String[] args) throws IOException {
//		try {
//			
////			getLatestArticles("World");
//			
//			getMostTrending("persons");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static JSONObject getLatestArticles(String query) throws IOException, ParseException {
		JSONObject result = new JSONObject();
		Date currentDate = null;
		int bestRated = 0;
		String topicImage = "";
		int currentUrgency = 0;
		
		Set<String> blacklist = new HashSet<>();
		blacklist.add("The following are the day's top                    general news");
		
		URL url = new URL(
				"http://ipool-extern.s.asideas.de:9090/api/v2/search?q=%22"
						+ URLEncoder.encode(query) + "%22&sortBy=dateCreated&limit=50");
		URLConnection connection = url.openConnection();
		JSONArray data = new JSONArray();

//		String responseBody = EntityUtils.toString(res.getEntity(), HTTP.UTF_8);
//		String line;
//		StringBuilder builder = new StringBuilder();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(
//				connection.getInputStream(), "UTF-8"));
//		while ((line = reader.readLine()) != null) {
//			
//			builder.append(new String(line.getBytes(), "UTF-8"));
//		}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
		
		IOUtils.copy(connection.getInputStream(), writer, "UTF-8");
		writer.close();
		os.close();
		String ciao = new String( os.toByteArray(), "UTF-8" );
	
//		System.out.println(ciao);
		JSONObject json = new JSONObject(ciao);
		JSONObject nextObj = new JSONObject();
		
		for (int i = 0; i < json.getJSONArray("documents").length(); i++) {
			int urgency = 0;
			JSONObject newObject = new JSONObject();
			JSONObject obj = json.getJSONArray("documents").getJSONObject(i);
			if(!blacklist.contains(obj.getJSONArray("captions").get(0))){
			String contentEncoded = new String(((String) obj.get("content")).getBytes("UTF-8")).replaceAll("<.+?>", "");
			
			String noTags = obj.get("publishedURL").toString().replaceAll("<.+?>", "");
			System.out.println(noTags);
			
//			 ByteArrayOutputStream b = new ByteArrayOutputStream();
//		        ObjectOutputStream o = new ObjectOutputStream(b);
//		        o.writeObject(noTags);
//		       byte[] bytes =  b.toByteArray();
			
		       
			
//			System.out.println(noTags);
			newObject.put("url", noTags);
			newObject.put("title", obj.get("title"));
			
			String imageURL = "";
			

			if (obj.has("medias")) {
				JSONArray medias = obj.getJSONArray("medias");
				
				for (int j = 0; j < medias.length(); j++) {
					JSONObject media = medias.getJSONObject(j);
					if (("PICTURE").equals(media.get("type"))) {
						imageURL = media.getJSONArray("references")
								.getJSONObject(0).get("url").toString();
						break;
					}
				}
				newObject.put("image", imageURL);
			}
			Date date = new Date(Long.parseLong(obj.get("dateCreated")
					.toString()));
			if(obj.has("urgency")){
				urgency = Integer.parseInt(obj.get("urgency").toString());
			}
			
			if(urgency >= bestRated && !imageURL.isEmpty()){
				topicImage = imageURL;
				bestRated = urgency;
			}
			
			SimpleDateFormat dt = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			
			Date dateParsed = dt.parse(date.toString());
			System.out.println(dateParsed.toString());
			newObject.put("date", dateParsed);
			
			if(currentDate != null){
				if(currentDate.getDay() == date.getDay() && currentDate.getMonth() == date.getMonth() && currentDate.getYear() == date.getYear()){
					
					if(currentUrgency < urgency){
						currentUrgency = urgency;
						nextObj = newObject;
					}
				}
				else {
					data.put(nextObj);
					currentUrgency = urgency;
					nextObj = newObject;
					currentDate = date;
				}
				
			}
			else {
				currentDate = date;
				currentUrgency = urgency;
				nextObj = newObject;
			}
			
			
		}}
		 data.put(nextObj);
		result.put("data", data);
		result.put("topicImage", topicImage);
		
		
//		for (int k = 0; k < data.length() && k < 10; k++) {
//			System.out.println(data.getJSONObject(k).get("title") + "     " +  data.getJSONObject(k).get("date"));
//			
//		}
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		
//		for (int k = 0; k < json.getJSONArray("documents").length() && k < 20; k++) {
//			
//			System.out.println(json.getJSONArray("documents").getJSONObject(k).get("title") + "     " +  new Date(Long.parseLong(json.getJSONArray("documents").getJSONObject(k).get("dateCreated").toString())));
//			if(json.getJSONArray("documents").getJSONObject(k).has("urgency"))
//			System.out.println(json.getJSONArray("documents").getJSONObject(k).get("urgency"));
//		}
//		System.out.println(result);

		return result;
	}
	
	
	
	public static JSONObject getMostTrending(String topic) throws IOException, ParseException{
		
		URL url = new URL(
				"http://ipool-extern.s.asideas.de:9090/api/v2/topics/trending?entityTypes=%22" + topic + "%22&limit=1");
		URLConnection connection = url.openConnection();

		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			builder.append(line);
		}
		JSONArray json = new JSONArray(builder.toString());
		
		String name = json.getJSONObject(0).getString("name");
				
		
		System.out.println(getLatestArticles(StringEscapeUtils.escapeHtml4(name)));
		
		return getLatestArticles(name);
		
		
	}

}
