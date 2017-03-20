import java.sql.*;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class monitor {

	public static void main(String[] args) throws SQLException, InterruptedException, MqttException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bugzilladb", "root", "12345678");
		Statement statement = connection.createStatement();
		ResultSet initial = statement.executeQuery("select count(bug_id) from bugs");
		initial.next(); 
		int record = initial.getInt("count(bug_id)"); 
		for(;;) {
			ResultSet result = statement.executeQuery("select count(bug_id) from bugs");
			result.next(); 
			int num = result.getInt("count(bug_id)");
			System.out.println("current: " + num);
			if(num != record) {
				System.out.println("NEW!");
				MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
		        client.connect();
		        MqttMessage message = new MqttMessage();
		        message.setPayload("NEW".getBytes());
		        client.publish("TRS", message);
		        client.disconnect();
				record = num;
			}
			Thread.sleep(1000);
		}
	}

}
