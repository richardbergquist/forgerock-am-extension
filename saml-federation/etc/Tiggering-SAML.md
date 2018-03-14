
# Endpoints to Trigger SP Initiated SSO on the OpenAM SP.

## Using SAML2 SSO in standalone mode to trigger via a hosted URL.
 See : [Saml2 Guide for triggering in standalone Modules](https://backstage.forgerock.com/docs/am/5/saml2-guide/#saml2-standalone-mode)

### **Version 1**

`
http://nzakdot1043rzrw-front.elinux.domain.co.nz:8080/openam/spssoinit?idpEntityID=http%3A%2F%2Fnzakdot1043rzrw-front.elinux.domain.co.nz%3A8080%2Fopenam&metaAlias=/MyRealm/sp&binding=HTTP-POST&RelayState=http%3A%2F%2Fforgerock.com&ForceAuthn=true
`

* This triggers an SP initiated SSO with a
  * SAML binding is HTTP-POST
  * To the OpenAM hosted IDP:  http://nzakdot1043rzrw-front.elinux.domain.co.nz:8080/openam   
  * From the OpenAM hosted SP with metaAlias=/MyRealm/sp
  * With `ForceAuthn=true`
  * Set RelayState=http%3A%2F%2Fforgerock.com to redirect to a custom URL.


### **Version 2**

`http://nzakdot1043rzrw-front.elinux.domain.co.nz:8080/openam/spssoinit?idpEntityID=http%3A%2F%2Fnzakdot1043rzrw-front.elinux.domain.co.nz%3A8080%2Fopenam&metaAlias=/MyRealm/sp&binding=HTTP-POST&ForceAuthn=true`

* This triggers an SP initiated SSO with a
  * SAML binding is HTTP-POST
  * to the OpenAM hosted IDP:  `http://nzakdot1043rzrw-front.elinux.domain.co.nz:8080/openam`
  * from the OpenAM hosted SP with `metaAlias=/MyRealm/sp`
  * With `ForceAuthn=true`
  * With no  RelayState set. Uses the 'Default Relay State URL' set in the SP config in AM.

### Progress Notes
#### @09/03/2018
 * Working on looking into doing this with a `integrated mode` style with OpenAM authentication modules - so another URL to trigger should be published, but for now to get up and running use the `standalone` mode above.
 * Still working on getting the OpenAM hosted SP to pass back identity information of the authenticated user. Working on some SAML2 plugins to achieve this. Right now the login just works, but its unclear what information is passed back to the calling URL (in the relay state).
 However since earlier this week we have sorted the pattern to compile against an external ForgeRock maven repository to create our own plugin extension code. So we plan to leverate this effort to extend the default SAML behaviour to pass back more interesting identity information to the calling web app.
 
 #### @09/03/2018 1pm
 On re-direct back from the SP a plugin has been created to add into a response header relevent user identity information from the SAML response assertion.
 
 A header like :
 
  `FederatedUserIdentity:eyJ1aWQiOiIwM2I2MDkwMi1jOTk5LTQxMzUtOGQyZi1hZmVkZjE2Y2ZmNjciICwgIk5hbWVJREZvcm1hdCI6InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpuYW1laWQtZm9ybWF0OnBlcnNpc3RlbnQiICwgIm1haWwiOiJ0ZXN0LmFscGhhMUBtYWlsLmNvbSIgLCAiTmFtZUlEIjoiclBIeDJjRnMwY05RZ3JPRzRVWFRESWJBbk5DQyIgLCAiZ2l2ZW5OYW1lIjoiVGVzdCIgLCAiTmFtZUlEUXVhbGlmaWVyIjoiaHR0cDovL256YWtkb3QxMDQzcnpydy1mcm9udC5lbGludXgud2VzdHBhYy5jby5uejo4MDgwL29wZW5hbSIgLCAiY24iOiJUZXN0LkRlbHRhMiIgLCAic24iOiJBbHBoYSIgfQ==`
 
 is returned. If the value is base64 decoded then the following type of information is found:
 
 `{ 
   "NameIDFormat":"urn:oasis:names:tc:SAML:2.0:nameid-format:persistent" , 
   "mail":"test.alpha1@mail.com", 
   "NameID":"rPHx2cFs0cNQgrOG4UXTDIbAnNCC", 
   "givenName":"Test", 
   "NameIDQualifier":"http://nzakdot1043rzrw-front.elinux.domain.co.nz:8080/openam", 
   "cn":"Test.Delta2",
   "sn":"Alpha" 
 } `
   
Notes:
 * The name of the header is configurable.
 * The types of identity information returned is also configurable.
 * The idea of this is to relay the logged in user identity information back to the calling web app. Optionally a cookie could be also dropped if requried.
 
 #### @02/03/2018
 ##### Obtaining SAML attributes out of the OpenAM login session: 
 
 Instead of relying on response headers another pattern is presented. 
 
 The SAML2 login will return a login cookie called : iPlanetDirectoryPro. The value of the cookie can be used in subsequent REST calls to OpenAM to query session information.
 
 After configuring AM as [per the following ForgeRock backstage KB article](https://backstage.forgerock.com/knowledge/kb/article/a72365672) a rest call can me made as follows, where the returned iPlanetDirectoryPro cookie (shown below as abbreviated) is passed in:
 
 `
  curl -X POST  \
  'http://nzakdot1043rzrw-front.elinux.domain.co.nz:8080/openam/json/MyRealm/sessions/?_action=getSessionInfo'   \
   -H 'cache-control: no-cache'  \
   -H 'content-type: application/json'  \
   -H 'iplanetdirectorypro: AQIC5...AJTMQAA*'
  `
 
 Returns:
 
`
  { "username":"Test.Delta2",
    "universalId":"id=Test.Delta2,ou=user,o=MyRealm,ou=services,dc=openam,dc=forgerock,dc=org",
    "realm":"/MyRealm",   
    "latestAccessTime":"2018-03-11T20:14:28Z",
    "maxIdleExpirationTime":"2018-03-11T20:44:28Z",
    "maxSessionExpirationTime":"2018-03-11T22:14:27Z",
    "properties":{ 
      "am.protected.givenName":"Test",
      "am.protected.mail":"test.alpha1@mail.com",
      "am.protected.cn":"Test.Delta2",
      "am.protected.sn":"Alpha" 
    }
  }
`

 Where the properties are the SAML attributes returned in the SAML Assertion. In this case each property name is prefixed with `am.protected.`
 
 
 
 