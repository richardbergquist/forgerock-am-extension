package nz.co.bergquistr.forgerock.am;

import com.sun.identity.authentication.modules.hotp.SMSGateway;
import com.sun.identity.shared.debug.Debug;
import java.util.Map;
import com.sun.identity.shared.datastruct.CollectionHelper;
import com.iplanet.am.util.AMSendMail;
import com.sun.identity.authentication.spi.AuthLoginException;

/**
 * nz.co.bergquistr.forgerock.am.EmailGateway
 */
public class EmailGateway implements SMSGateway {

  protected Debug debug = null;
  private static String SMTPHOSTNAME = "sunAMAuthHOTPSMTPHostName";
  private static String SMTPHOSTPORT = "sunAMAuthHOTPSMTPHostPort";
  private static String SMTPUSERNAME = "sunAMAuthHOTPSMTPUserName";
  private static String SMTPUSERPASSWORD = "sunAMAuthHOTPSMTPUserPassword";
  private static String SMTPSSLENABLED = "sunAMAuthHOTPSMTPSSLEnabled";

  String smtpHostName = null;
  String smtpHostPort = null;
  String smtpUserName = null;
  String smtpUserPassword = null;
  String smtpSSLEnabled = null;
  boolean sslEnabled = true;
  private boolean startTls = false;

  /**
   * Default constructor. Initialise debugger.
   */
  public EmailGateway() {
    debug = Debug.getInstance("amAuthHOTP");
  }


  /**
   * Configures the email gateway with server options.
   * <p>
   *
   * @param options The Map of server connection options.
   */
  private void setOptions(Map options) {
    smtpHostName = CollectionHelper.getMapAttr(options, SMTPHOSTNAME);
    smtpHostPort = CollectionHelper.getMapAttr(options, SMTPHOSTPORT);
    smtpUserName = CollectionHelper.getMapAttr(options, SMTPUSERNAME);
    smtpUserPassword = CollectionHelper.getMapAttr(options, SMTPUSERPASSWORD);
    smtpSSLEnabled = CollectionHelper.getMapAttr(options, SMTPSSLENABLED);

    if (smtpSSLEnabled != null) {
      if (smtpSSLEnabled.equals("Non SSL")) {
        sslEnabled = false;
      } else if (smtpSSLEnabled.equals("Start TLS")) {
        sslEnabled = false;
        startTls = true;
      }
    }
  }

  /**
   * Sends a SMS message to the phone with the code.
   * <p>
   *
   * @param from The address that sends the SMS message
   * @param to The address that the SMS message is sent
   * @param subject The SMS subject
   * @param message The content contained in the SMS message
   * @param code The code in the SMS message
   * @param options The SMS gateway options defined in the HOTP authentication
   * module
   * @throws AuthLoginException In case the module was unable to send the SMS
   */
  public void sendSMSMessage(String from, String to, String subject, String message, String code, Map options) throws AuthLoginException {

    System.out.println("--> EmailGateway.sendSMSMessage - HAS BEEN CALLED !");
    debug.message("--> EmailGateway.sendSMSMessage - HAS BEEN CALLED !");

    if (to == null) {
      return;
    }
    try {
      setOptions(options);
      String msg = message + code;
      String tos[] = new String[1];
      // If the phone does not contain provider info, append ATT to it
      // Note : need to figure out a way to add the provider information
      // For now assume : the user phone # entered is
      // <phone@provider_address). For exampe : 4080989109@txt.att.net
      if (to.indexOf("@") == -1) {
        to = to + "@txt.att.net";
      }
      tos[0] = to;
      AMSendMail sendMail = new AMSendMail();

      if (smtpHostName == null || smtpHostPort == null) {
        sendMail.postMail(tos, subject, msg, from);
      } else {
        sendMail.postMail(tos, subject, msg, from, "UTF-8", smtpHostName, smtpHostPort, smtpUserName, smtpUserPassword, sslEnabled, startTls);
      }
      //if (debug.messageEnabled()) {
      debug.message("EmailGateway.sendSMSMessage() : " + "HOTP sent to : " + to + ".");
      //}
    } catch (Exception e) {
      debug.error("EmailGateway.sendSMSMessage() : " + "Exception in sending HOTP code : " , e);
      throw new AuthLoginException("Failed to send OTP code to " + to, e);
    }

  }

  /**
   * Sends a SMS message to the phone with the code.
   * <p>
   *
   * @param from The address that sends the SMS message
   * @param to The address that the SMS message is sent
   * @param subject The SMS subject
   * @param message The content contained in the SMS message
   * @param code The code in the SMS message
   * @param options The SMS gateway options defined in the HOTP authentication
   * module
   * @throws AuthLoginException In case the module was unable to send the SMS
   */
  public void sendEmail(String from, String to, String subject, String message, String code, Map options) throws AuthLoginException {

    System.out.println("--> EmailGateway.sendEmail - HAS BEEN CALLED !");
    debug.message("--> EmailGateway.sendEmail - HAS BEEN CALLED !");

    sendSMSMessage(from, to, subject, message, code, options);
  }
}
