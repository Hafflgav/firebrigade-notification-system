package com.firebrigade.CamundaWorker;

import com.firebrigade.Mail.Mail;
import com.firebrigade.Slack.SlackApp;
import com.firebrigade.Timer.Time;
import com.firebrigade.Twilio.TwilioSMSMessage;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Start porcess with
 * - name
 * - email
 * - mobilenumbers[]
 * - fireService_over
 */

@SpringBootApplication
@EnableZeebeClient
public class EmergencyNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmergencyNotificationApplication.class, args);
	}

	@ZeebeWorker(type = "capture_time_worker")
	public void handleJob_capture_time(final JobClient client, final ActivatedJob job) {
		Time timer= new Time();
		client.newCompleteCommand(job.getKey())
				.variables("{\"startingTime\":"+ "\""+ timer.getTime()+"\"}")
				.send()
				.exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
	}

	@ZeebeWorker(type = "notify_mail_worker")
	public void handleJob_notify_mail(final JobClient client, final ActivatedJob job) {
		String mailAddress = (String) job.getVariablesAsMap().get("email");
		String name = (String) job.getVariablesAsMap().get("name");
		String fireService_over = (String) job.getVariablesAsMap().get("fireService_over");

		Mail mail = new Mail();
		if(fireService_over.equals("false")){
			mail.sendTLSMail(mailAddress, name, " is called for fire-service duty and will not be available until further notice. " +
					"\n This is a generated mail. Please do not answer.");
		}
		else if (!fireService_over.equals("false")){
			String timeAway = (String) job.getVariablesAsMap().get("timeDifference");
			mail.sendTLSMail(mailAddress, name, " is back from fire-service. He spend "+timeAway+" minutes on duty. Pleas reach out via Slack if you do have further questions. " +
					"\n This is a generated mail. Please do not answer.\"");
		}

		client.newCompleteCommand(job.getKey())
				.variables("")
				.send()
				.exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
	}

	@ZeebeWorker(type = "notify_slack_worker")
	public void handle_job_notify_slack(final JobClient client, final ActivatedJob job) {
		SlackApp slack = new SlackApp();
		String fireService_over = (String) job.getVariablesAsMap().get("fireService_over");
		String name = (String) job.getVariablesAsMap().get("name");

		if(fireService_over.equals("false")){
			slack.sendSlackMessage( name, " is called for fire-service duty and will not be available until further notice.");
		}
		else if (!fireService_over.equals("false")){
			String timeAway = (String) job.getVariablesAsMap().get("timeDifference");
			slack.sendSlackMessage(name, " is back from fire-service. He spend "+timeAway+" minutes on duty.");
		}

		client.newCompleteCommand(job.getKey())
				.variables("")
				.send()
				.exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
	}

	@ZeebeWorker(type = "notify_sms_worker")
	public void handleJob_notify_sms(final JobClient client, final ActivatedJob job) {
		ArrayList<String> recipients = (ArrayList<String>) job.getVariablesAsMap().get("mobilenumbers");
		String fireService_over = (String) job.getVariablesAsMap().get("fireService_over");
		String name = (String) job.getVariablesAsMap().get("name");

		TwilioSMSMessage smsMessage= new TwilioSMSMessage();

		if(fireService_over.equals("false")){
			smsMessage.sendMessage(recipients, name +" was just called to duty for Fire Service and will not be available until further notice. This is an automated notification.");
		}
		else if (!fireService_over.equals("false")){
			String timeAway = (String) job.getVariablesAsMap().get("timeDifference");
			smsMessage.sendMessage(recipients, name + " is back from fire-service. He spend "+timeAway+" minutes on duty.");
		}

		client.newCompleteCommand(job.getKey())
				.variables("")
				.send()
				.exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
	}

	@ZeebeWorker(type = "calculate_time_difference_worker")
	public void handleJob_calculate_time_difference(final JobClient client, final ActivatedJob job) {
		Time timer= new Time();
		String startingTimeString = (String) job.getVariablesAsMap().get("startingTime");
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime dateTime = LocalDateTime.parse(startingTimeString, formatter);

		client.newCompleteCommand(job.getKey())
				.variables("{\"timeDifference\":"+ "\""+ timer.calculateTimeDifference(dateTime, timer.getTime())+"\"}")
				.send()
				.exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
	}
}
