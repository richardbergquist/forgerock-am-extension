package nz.co.bergquistr.forgerock.am;


//import com.sun.identity.authentication.modules.hotp.DefaultSMSGatewayImpl;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.modules.hotp.SMSGateway;
import com.sun.identity.shared.debug.Debug;

import java.util.Map;

public class BlankSMSGateway implements SMSGateway {

  protected Debug debug = null;

  public BlankSMSGateway() {
    this.debug = Debug.getInstance("amAuthHOTP");
  }

  /**
   * Sends a SMS message to the phone with the code
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

    if (to == null) {
      return;
    }

    if (debug.messageEnabled()) {
      debug.message("BlankSMSGateway.sendSMSMessage() : " +  " HOTP sending to : " + to + ".");
    }
  }

  /**
   * Sends an email  message to the mail with the code
   * <p>
   *
   * @param from The address that sends the E-mail message
   * @param to The address that the E-mail message is sent
   * @param subject The E-mail subject
   * @param message The content contained in the E-mail message
   * @param code The code in the E-mail message
   * @param options The SMS gateway options defined in the HOTP authentication
   * module
   * @throws AuthLoginException In case the module was unable to send the e-mail
   */
  public void sendEmail(String from, String to, String subject, String message, String code, Map options) throws AuthLoginException {
    sendSMSMessage(from, to, subject, message, code, options);
  }


}
