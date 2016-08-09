import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

public class GoEuroTest {
	
	public class Location {
		int _id;
		String name;
		String type;
		GeoPosition geo_position;
		
		@Override
		public String toString() {
			return _id + "," + name + "," + type + "," +geo_position;
		}
	}
	
	public class GeoPosition {
		double latitude;
		double longitude;
		
		@Override
		public String toString() {
			return latitude + "," + longitude;
		}
	}
	
	public static String getHTML(String urlToRead) throws IOException {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Must enter one argument");
			System.exit(1);
		}
		
		String cityName = args[0];
		// allow city names with more than one word
		cityName = cityName.replaceAll("\\s+", "%20");
		
		String url = "http://api.goeuro.com/api/v2/position/suggest/en/" + cityName;
		String result = getHTML(url);
		
		Gson gson = new Gson();
		Location[] locArray = gson.fromJson(result, Location[].class);
		
		CSVWriter writer = new CSVWriter(new FileWriter("output.csv"));
		String[] columnNames = {"_id", "name", "type", "latitude", "longitude"};
		writer.writeNext(columnNames);
		for (Location loc : locArray) {
			writer.writeNext(loc.toString().split(","));
		}
		
		writer.close();
	}
}
