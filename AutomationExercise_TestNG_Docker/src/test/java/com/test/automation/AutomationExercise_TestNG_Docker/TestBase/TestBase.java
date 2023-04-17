package com.test.automation.AutomationExercise_TestNG_Docker.TestBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;

import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import test.automationframework.Notification.EmailTestExecutionReports;
import test.automationframework.Utils.Efficacies;

public class TestBase {

	Properties emailProperty;

	@BeforeSuite(alwaysRun = true)
	@Parameters({ "emailConfiguration" })
	public void intializeEmailConfiguration(String emailConfiguration) throws IOException {
		emailProperty = new Efficacies().loadPropertyFile(emailConfiguration);
	}

	@BeforeTest(alwaysRun = true)
	@Parameters({ "browser" })
	public void setDockerContainers(String browser) throws IOException, InterruptedException {
		String filePath = System.getProperty("user.dir") + File.separator + "Scripts";
		ProcessBuilder pb = null;
		String ymlFile = null;
		String scalupService = null;
		boolean isContainerUp = false;

		if (browser.equalsIgnoreCase("chrome")) {
			ymlFile = "SetupSeleniumGridChrome.yml";
			scalupService = "ChromeService";
		} else if (browser.equalsIgnoreCase("firefox")) {
			ymlFile = "SetupSeleniumGridFireFox.yml";
			scalupService = "FirefoxService";
		} else if (browser.equalsIgnoreCase("Edge")) {
			ymlFile = "SetupSeleniumGridEdge.yml";
			scalupService = "EdgeService";
		}

		if (System.getProperty("os.name").contains("Mac")) {
			// Making Shell Script Executable
			Process batch = Runtime.getRuntime()
					.exec("chmod a=wxr " + filePath + File.separator + "startDockerContainers.sh");
			batch.waitFor();
			String[] cmd = new String[] { "bash", "Scripts" + File.separator + "startDockerContainers.sh", ymlFile,
					scalupService };
			pb = new ProcessBuilder(cmd);
		} else if (System.getProperty("os.name").contains("Windows")) {
			String[] cmd = new String[] { "Scripts" + File.separator + "startDockerContainers.bat", ymlFile,
					scalupService };
			pb = new ProcessBuilder(cmd);
		}

		try {
			Process p = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			while ((s = reader.readLine()) != null) {
				System.out.println(s);
				if (s.contains("Up") && (s.contains("minutes") || s.contains("seconds")) && s.contains(scalupService))
					isContainerUp = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isContainerUp) {
			System.out.println("====================== Docker Containers are Up and Running ================");
			Thread.sleep(5000);
		} else
			throw new SkipException(
					"=================== Docker Containers are not running, some issue is there ========================");
	}

	@AfterTest(alwaysRun = true)
	@Parameters({ "browser" })
	public void shutDownDockerContainers(String browser) throws IOException, InterruptedException {
		String filePath = System.getProperty("user.dir") + File.separator + "Scripts";
		ProcessBuilder pb = null;
		String ymlFile = null;
		String scalupService = null;
		boolean isContainerDown = false;

		if (browser.equalsIgnoreCase("chrome")) {
			ymlFile = "SetupSeleniumGridChrome.yml";
			scalupService = "ChromeService";
		} else if (browser.equalsIgnoreCase("firefox")) {
			ymlFile = "SetupSeleniumGridFireFox.yml";
			scalupService = "FirefoxService";
		} else if (browser.equalsIgnoreCase("Edge")) {
			ymlFile = "SetupSeleniumGridEdge.yml";
			scalupService = "EdgeService";
		}

		if (System.getProperty("os.name").contains("Mac")) {
			// Making Shell Script Executable
			Process batch = Runtime.getRuntime()
					.exec("chmod a=wxr " + filePath + File.separator + "stopDockerContainers.sh");
			batch.waitFor();
			String[] cmd = new String[] { "bash", "Scripts" + File.separator + "stopDockerContainers.sh", ymlFile,
					scalupService };
			pb = new ProcessBuilder(cmd);
		} else if (System.getProperty("os.name").contains("Windows")) {
			String[] cmd = new String[] { "Scripts" + File.separator + "stopDockerContainers.bat", ymlFile,
					scalupService };
			pb = new ProcessBuilder(cmd);
		}

		try {
			Process p = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			while ((s = reader.readLine()) != null) {
				System.out.println(s);
				if (!s.contains(scalupService))
					isContainerDown = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isContainerDown)
			System.out.println("====================== Docker Containers are down ================");
		else
			throw new SkipException(
					"=================== Docker Containers are still running, some issue is there ========================");
	}
	
	@AfterSuite
	public void emailSentLogic() throws InterruptedException {
		EmailTestExecutionReports email = new EmailTestExecutionReports(emailProperty);
		Session session = email.setBasicEmailConfiguration().createNewEmailSession();
		Message msg;
		try {
			msg = email.setEmailMsgContent(session);
			email.sendEmail(msg, emailProperty.getProperty("executionReport"));
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}
