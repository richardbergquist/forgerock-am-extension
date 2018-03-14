package nz.co.bergquistr.forgerock.am.federation.saml2.plugins;

import com.sun.identity.saml2.assertion.Assertion;
import com.sun.identity.saml2.assertion.Attribute;
import com.sun.identity.saml2.assertion.AttributeStatement;
import com.sun.identity.saml2.assertion.Subject;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.plugins.SAML2ServiceProviderAdapter;
import com.sun.identity.saml2.protocol.AuthnRequest;
import com.sun.identity.saml2.protocol.LogoutRequest;
import com.sun.identity.saml2.protocol.LogoutResponse;
import com.sun.identity.saml2.protocol.ManageNameIDRequest;
import com.sun.identity.saml2.protocol.ManageNameIDResponse;
import com.sun.identity.saml2.protocol.Response;
import com.sun.identity.shared.debug.Debug;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

/**
 * A custom implementation of a SAML2ServiceProviderAdapter to provide SP
 * custom handling via callbacks during the SAML2 process.
 * <p>
 *
 * @author richard bergquist
 */
public class CustomSAML2ServiceProviderAdapter extends com.sun.identity.saml2.plugins.SAML2ServiceProviderAdapter {

  private static final String DEBUGHDR = "CustomSAML2ServiceProviderAdapter.";
  private static final String DEFAULT_RESPONSE_HDR = "FederatedUserIdentity";
  private static final String CONFIG_KEY_RESPONSE_HDR = "ResponseHeader";

  protected Debug debug = null;
  protected Map initParams = null;
  protected String responseHeader = null;

  /**
   * Writes the request headers to the debugger.
   *
   * @param location The debugging code location.
   * @param request The HttpServletRequest
   */
  private void debugHeaders(String location, HttpServletRequest request) {
    Enumeration headerNamesEnum = request.getHeaderNames();
    while (headerNamesEnum.hasMoreElements()) {
      String headerName = (String) headerNamesEnum.nextElement();
      String headerValue = request.getHeader(headerName);
      debug.message(String.format(location + " header %s : %s", headerName, headerValue));
    }
  }

  /*
   * Writes the SAML2 SSO Response assertions to the debugger.
   *
   * @param location The debugging code location.
   * @param ssoResponse The com.sun.identity.saml2.protocol.Response
   * @throws SAML2Exception On error extracting the XML from the assertion.
   *
  private void debugAssertions(String location, Response ssoResponse) throws SAML2Exception {
    List assertions = ssoResponse.getAssertion();
    ListIterator assertionIterator = assertions.listIterator();
    while (assertionIterator.hasNext()) {
      Assertion assertion = (Assertion) assertionIterator.next();
      String assertionXML = assertion.toXMLString();
      debug.message(String.format(location + " assertionXML: %s ", assertionXML));
    }
  }*/

  /**
   * Returns the response header to use to return the user attributes.
   * First look up from the supplied init params for the key "ResponseHeader".
   * If not found set it to a default of "FederatedUserIdentity".
   *
   * @return the response header.
   */
  private String getResponseHeaderName() {

    if (this.responseHeader == null) {
      this.responseHeader = (String) this.initParams.get(CONFIG_KEY_RESPONSE_HDR);
      if (this.responseHeader == null) {
        this.responseHeader = DEFAULT_RESPONSE_HDR;
      }
    }
    return this.responseHeader;
  }

  /**
   * Default constructor. Initialise debugger.
   */
  public CustomSAML2ServiceProviderAdapter() {
    debug = Debug.getInstance("libSAML2");
  }


  /**
   * Initializes the federation adapter, this method will only be executed
   * once after creation of the adapter instance.
   * @param initParams  initial set of parameters configured in the service
   * 		provider for this adapter. One of the parameters named
   *          <code>HOSTED_ENTITY_ID</code> refers to the ID of this
   *          hosted service provider entity, one of the parameters named
   *          <code>REALM</code> refers to the realm of the hosted entity.
   */
  @Override
  public void initialize(Map initParams) {
    debug.message(String.format(DEBUGHDR + ".initialize: called with params:%s", initParams.toString()));
    this.initParams = initParams;
  }

  /**
   * Invokes before OpenAM sends the
   * Single-Sign-On request to IDP.
   * @param hostedEntityID entity ID for the hosted SP
   * @param idpEntityID entity id for the IDP to which the request will
   * 		be sent. This will be null in ECP case.
   * @param realm Realm of the hosted SP.
   * @param request servlet request
   * @param response servlet response
   * @param authnRequest the authentication request to be send to IDP
   * @exception SAML2Exception if user want to fail the process.
   */
  public void preSingleSignOnRequest(
          String hostedEntityID,
          String idpEntityID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response,
          AuthnRequest authnRequest)
          throws SAML2Exception {

    String debugMethod = "preSingleSignOnRequest";
    debug.message(String.format(DEBUGHDR + debugMethod + " called."));
    debug.message(String.format(DEBUGHDR + debugMethod + " hostedEntityID: %s idpEntityID: %s realm: %s authnRequest: %s", hostedEntityID, idpEntityID, realm, authnRequest.toXMLString()));
    debugHeaders(DEBUGHDR + debugMethod, request);

    return;
  }


  /**
   * Invokes when the <code>FAM</code> received the Single-Sign-On response
   * from the IDP, this is called before any processing started on SP side.
   * @param hostedEntityID entity ID for the hosted SP
   * @param realm Realm of the hosted SP.
   * @param request servlet request
   * @param response servlet response
   * @param authnRequest the original authentication request sent from SP,
   *       null if this is IDP initiated SSO.
   * @param ssoResponse response from IDP
   * @param profile protocol profile used, one of the following values:
   *     <code>SAML2Constants.HTTP_POST</code>,
   *     <code>SAML2Constants.HTTP_ARTIFACT</code>,
   *     <code>SAML2Constants.PAOS</code>
   * @exception SAML2Exception if user want to fail the process.
   */
  public void preSingleSignOnProcess(
          String hostedEntityID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response,
          AuthnRequest authnRequest,
          Response ssoResponse,
          String profile)
          throws SAML2Exception {

    String debugMethod = "preSingleSignOnProcess";
    debug.message(String.format(DEBUGHDR + debugMethod + " hostedEntityID: %s realm: %s ssoResponseXML: %s", hostedEntityID, realm, ssoResponse.toXMLString()));
    //debugHeaders(DEBUGHDR + debugMethod, request);
    //debugAssertions(DEBUGHDR + debugMethod, ssoResponse);

    return;
  }

  /**
   * Invokes after Single-Sign-On processing succeeded.
   * @param hostedEntityID Entity ID for the hosted SP
   * @param realm Realm of the hosted SP.
   * @param request servlet request
   * @param response servlet response
   * @param out the print writer for writing out presentation
   * @param session user's session
   * @param authnRequest the original authentication request sent from SP,
   *       null if this is IDP initiated SSO.
   * @param ssoResponse response from IDP
   * @param profile protocol profile used, one of the following values:
   *     <code>SAML2Constants.HTTP_POST</code>,
   *     <code>SAML2Constants.HTTP_ARTIFACT</code>,
   *     <code>SAML2Constants.PAOS</code>
   * @param isFederation true if this is federation case, false otherwise.
   * @return true if browser redirection happened after processing,
   *     false otherwise. Default to false.
   * @exception SAML2Exception if user want to fail the process.
   */
  public boolean postSingleSignOnSuccess(
          String hostedEntityID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response,
          PrintWriter out,
          Object session,
          AuthnRequest authnRequest,
          Response ssoResponse,
          String profile,
          boolean isFederation)
          throws SAML2Exception {

    String debugMethod = "postSingleSignOnSuccess";

    debug.message(String.format(DEBUGHDR + debugMethod + " called."));
    String ssoResponseXML = ssoResponse.toXMLString();
    //debug.message(String.format(DEBUGHDR + debugMethod + " hostedEntityID: %s realm: %s profile: %s", hostedEntityID, realm, profile));
    //debug.message(String.format(DEBUGHDR + debugMethod + "ssoResponseXML: %s", ssoResponseXML));

    String nameID = null;
    String nameIDFormat = null;
    String nameIDQualifier = null;

    HashMap<String,String> attributeMap = new HashMap<>();

    List assertions = ssoResponse.getAssertion();
    ListIterator assertionIterator = assertions.listIterator();
    while (assertionIterator.hasNext()) {
      Assertion assertion = (Assertion) assertionIterator.next();

      //Parse the NameID values into our attributeMap ...
      //    <saml:Subject>
      //        <saml:NameID Format="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"
      //                     NameQualifier="http://nzakdot1043rzrw-front.elinux.domain.co.nz:8080/openam"
      //                     SPNameQualifier="http://nzakdot1043rzrw-front.elinux.domain.co.nz:8080/openam">
      //            rPHx2cFs0cNQgrOG4UXTDIbAnNCC
      //        </saml:NameID> ...
      nameID = assertion.getSubject().getNameID().getValue();
      nameIDFormat = assertion.getSubject().getNameID().getFormat();
      nameIDQualifier = assertion.getSubject().getNameID().getNameQualifier();

      //Parsing of the attribute statements into our attributeMap ...
      List<AttributeStatement> attributeStatementList = assertion.getAttributeStatements();
      if (attributeStatementList != null) {
        for (AttributeStatement attributeStatement : attributeStatementList) {
          //debug.message(String.format(DEBUGHDR + debugMethod + " found an AttributeStatement... "));
          List<Attribute> attributeList = attributeStatement.getAttribute();
          if (attributeList != null) {
            for (Attribute attribute : attributeList) {

              String attributeName = attribute.getName();
              //debug.message(String.format(DEBUGHDR + debugMethod + " found an Attribute name: %s", attributeName));
              List values = attribute.getAttributeValueString();
              if (values != null) {
                StringBuffer valueBuffer = new StringBuffer();
                ListIterator valueIterator = values.listIterator();
                if (valueIterator != null) {
                  while (valueIterator.hasNext()){
                    String attributeValue = (String) valueIterator.next();
                    //debug.message(String.format(DEBUGHDR + debugMethod + " found an attribute value: %s for %s", attributeValue, attributeName));
                    if (valueBuffer.length() > 0) {
                      valueBuffer.append("|");
                    }
                    valueBuffer.append(attributeValue);
                  }
                }
                attributeMap.put(attributeName, valueBuffer.toString());
              }
            }
          }
        }
      }
    }
    debug.message(String.format(DEBUGHDR + debugMethod + " parsed attribute map: %s ", attributeMap.toString()));

    //Now our map is build we need to add it into the response headers.
    StringBuffer jsonAttributes = new StringBuffer("{");
    jsonAttributes.append("\"NameID\":\"" + nameID + "\", ");
    jsonAttributes.append("\"NameIDFormat\":\"" + nameIDFormat + "\", ");
    jsonAttributes.append("\"NameIDQualifier\":\"" + nameIDQualifier + "\"");

    Set<String> keySet = attributeMap.keySet();
    for (String key : keySet) {
      jsonAttributes.append(", \"" + key + "\":\"" + attributeMap.get(key) + "\"");
    }
    jsonAttributes.append("}");

    debug.message(String.format(DEBUGHDR + debugMethod + " jsonAttributes: %s ", jsonAttributes.toString()));
    byte[] encodedJsonBytes = Base64.getEncoder().encode(jsonAttributes.toString().getBytes());
    String encodedJson = new String(encodedJsonBytes);
    debug.message(String.format(DEBUGHDR + debugMethod + " encodedJson: %s ", encodedJson));

    //Finally add it into the response header.
    response.addHeader(getResponseHeaderName(), encodedJson);

    //Return true if browser redirection happened after processing, false otherwise.
    return false;
  }


  /**
   * Invokes after Single Sign-On processing failed.
   * @param hostedEntityID Entity ID for the hosted SP
   * @param realm Realm of the hosted SP.
   * @param request servlet request
   * @param response servlet response
   * @param authnRequest the original authentication request sent from SP,
   *       null if this is IDP initiated SSO.
   * @param ssoResponse response from IDP
   * @param profile protocol profile used, one of the following values:
   *     <code>SAML2Constants.HTTP_POST</code>,
   *     <code>SAML2Constants.HTTP_ARTIFACT</code>,
   *     <code>SAML2Constants.PAOS</code>
   * @param failureCode an integer specifies the failure code. Possible
   *          failure codes are defined in this interface.
   * @return true if browser redirection happened, false otherwise. Default to
   *         false.
   */
  public boolean postSingleSignOnFailure(
          String hostedEntityID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response,
          AuthnRequest authnRequest,
          Response ssoResponse,
          String profile,
          int failureCode) {

    String debugMethod = "postSingleSignOnFailure";
    debug.message(String.format(DEBUGHDR + debugMethod + " called with failureCode: %s", failureCode));

    return false;
  }


  /**
   * Invokes after new Name Identifier processing succeeded.
   * @param hostedEntityID Entity ID for the hosted SP
   * @param realm Realm of the hosted SP.
   * @param request servlet request
   * @param response servlet response
   * @param userID Universal ID of the user with whom the new name identifier
   *        request performed
   * @param idRequest New name identifier request, value will be
   *                null if the request object is not available
   * @param idResponse New name identifier response, value will be
   *		null if the response object is not available
   * @param binding Binding used for new name identifier request,
   *        one of following values:
   *		<code>SAML2Constants.SOAP</code>,
   *		<code>SAML2Constants.HTTP_REDIRECT</code>
   */
  public void postNewNameIDSuccess(
          String hostedEntityID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response,
          String userID,
          ManageNameIDRequest idRequest,
          ManageNameIDResponse idResponse,
          String binding) {
    return;
  }

  /**
   * Invokes after Terminate Name Identifier processing succeeded.
   * @param hostedEntityID Entity ID for the hosted SP
   * @param realm Realm of the hosted SP.
   * @param request servlet request
   * @param response servlet response
   * @param userID Universal ID of the user with whom name id termination
   *        performed.
   * @param idRequest Terminate name identifier request.
   * @param idResponse Terminate name identifier response, value will be
   *		null if the response object is not available
   * @param binding binding used for Terminate Name Identifier request,
   *      one of following values:
   *		<code>SAML2Constants.SOAP</code>,
   *		<code>SAML2Constants.HTTP_REDIRECT</code>
   */
  public void postTerminateNameIDSuccess(
          String hostedEntityID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response,
          String userID,
          ManageNameIDRequest idRequest,
          ManageNameIDResponse idResponse,
          String binding) {
    return;
  }

  /**
   * Invokes before single logout process started on <code>SP</code> side.
   * This method is called before the user session is invalidated on the
   * service provider side.
   * @param hostedEntityID Entity ID for the hosted SP
   * @param realm Realm of the hosted SP.
   * @param request servlet request
   * @param response servlet response
   * @param userID universal ID of the user
   * @param logoutRequest single logout request object
   * @param logoutResponse single logout response, value will be
   *          null if the response object is not available
   * @param binding binding used for Single Logout request,
   *      one of following values:
   *		<code>SAML2Constants.SOAP</code>,
   *		<code>SAML2Constants.HTTP_REDIRECT</code>
   * @exception SAML2Exception if user want to fail the process.
   */
  public void preSingleLogoutProcess(
          String hostedEntityID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response,
          String userID,
          LogoutRequest logoutRequest,
          LogoutResponse logoutResponse,
          String binding)
          throws SAML2Exception {
    return;
  }

  /**
   * Invokes after single logout process succeeded, i.e. user session
   * has been invalidated.
   * @param hostedEntityID Entity ID for the hosted SP
   * @param realm Realm of the hosted SP.
   * @param request servlet request
   * @param response servlet response
   * @param userID universal ID of the user
   * @param logoutRequest single logout request, value will be
   *          null if the request object is not available
   * @param logoutResponse single logout response, value will be
   *          null if the response object is not available
   * @param binding binding used for Single Logout request,
   *      one of following values:
   *		<code>SAML2Constants.SOAP</code>,
   *		<code>SAML2Constants.HTTP_REDIRECT</code>
   */
  public void postSingleLogoutSuccess(
          String hostedEntityID,
          String realm,
          HttpServletRequest request,
          HttpServletResponse response,
          String userID,
          LogoutRequest logoutRequest,
          LogoutResponse logoutResponse,
          String binding) {
    return;
  }
}
