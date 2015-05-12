package org.pocketcampus.plugin.authentication.server;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;

public class IosProvisionningProfiles {




    public static final String EMAIL_XML_EN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple Inc//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "    <dict>\n" +
            "        <key>PayloadVersion</key>\n" +
            "        <integer>1</integer>\n" +
            "        <key>PayloadUUID</key>\n" +
            "        <string>CONFIGURATION_PAYLOAD_UUID</string>\n" +
            "        <key>PayloadType</key>\n" +
            "        <string>Configuration</string>\n" +
            "        <key>PayloadIdentifier</key>\n" +
            "        <string>org.pocketcampus.epflmailconfig</string>\n" +
            "        <key>PayloadDisplayName</key>\n" +
            "        <string>EPFL email</string>\n" +
            "        <key>PayloadDescription</key>\n" +
            "        <string>Installs your EPFL email</string>\n" +
            "        <key>PayloadOrganization</key>\n" +
            "        <string>École Polytechnique Fédérale de Lausanne</string>\n" +
            "        <key>PayloadContent</key>\n" +
            "        <array>\n" +
            "            <dict>\n" +
            "                <key>EmailAddress</key>\n" +
            "                <string>USER_EMAIL</string>\n" +
            "                <key>Host</key>\n" +
            "                <string>ewa.epfl.ch</string>\n" +
            "                <key>SSL</key>\n" +
            "                <true/>\n" +
            "                <key>UserName</key>\n" +
            "                <string>USER_GASPAR@INTRANET</string>\n" +
            "                <key>MailNumberOfPastDaysToSync</key>\n" +
            "                <integer>365</integer>\n" +
            "                <key>PayloadDescription</key>\n" +
            "                <string>Installs your EPFL email</string>\n" +
            "                <key>PayloadUUID</key>\n" +
            "                <string>ACCOUNT_PAYLOAD_UUID</string>\n" +
            "                <key>PayloadType</key>\n" +
            "                <string>com.apple.eas.account</string>\n" +
            "                <key>PayloadDisplayName</key>\n" +
            "                <string>EPFL</string>\n" +
            "                <key>PayloadVersion</key>\n" +
            "                <integer>1</integer>\n" +
            "                <key>PayloadOrganization</key>\n" +
            "                <string>École Polytechnique Fédérale de Lausanne</string>\n" +
            "                <key>PayloadIdentifier</key>\n" +
            "                <string>org.pocketcampus.epflmailconfig</string>\n" +
            "            </dict>\n" +
            "        </array>\n" +
            "    </dict>\n" +
            "</plist>";


    public static final String EMAIL_XML_FR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple Inc//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "    <dict>\n" +
            "        <key>PayloadVersion</key>\n" +
            "        <integer>1</integer>\n" +
            "        <key>PayloadUUID</key>\n" +
            "        <string>CONFIGURATION_PAYLOAD_UUID</string>\n" +
            "        <key>PayloadType</key>\n" +
            "        <string>Configuration</string>\n" +
            "        <key>PayloadIdentifier</key>\n" +
            "        <string>org.pocketcampus.epflmailconfig</string>\n" +
            "        <key>PayloadDisplayName</key>\n" +
            "        <string>Email EPFL</string>\n" +
            "        <key>PayloadDescription</key>\n" +
            "        <string>Installe votre compte email EPFL</string>\n" +
            "        <key>PayloadOrganization</key>\n" +
            "        <string>École Polytechnique Fédérale de Lausanne</string>\n" +
            "        <key>PayloadContent</key>\n" +
            "        <array>\n" +
            "            <dict>\n" +
            "                <key>EmailAddress</key>\n" +
            "                <string>USER_EMAIL</string>\n" +
            "                <key>Host</key>\n" +
            "                <string>ewa.epfl.ch</string>\n" +
            "                <key>SSL</key>\n" +
            "                <true/>\n" +
            "                <key>UserName</key>\n" +
            "                <string>USER_GASPAR@INTRANET</string>\n" +
            "                <key>MailNumberOfPastDaysToSync</key>\n" +
            "                <integer>365</integer>\n" +
            "                <key>PayloadDescription</key>\n" +
            "                <string>Installe votre compte email EPFL</string>\n" +
            "                <key>PayloadUUID</key>\n" +
            "                <string>ACCOUNT_PAYLOAD_UUID</string>\n" +
            "                <key>PayloadType</key>\n" +
            "                <string>com.apple.eas.account</string>\n" +
            "                <key>PayloadDisplayName</key>\n" +
            "                <string>EPFL</string>\n" +
            "                <key>PayloadVersion</key>\n" +
            "                <integer>1</integer>\n" +
            "                <key>PayloadOrganization</key>\n" +
            "                <string>École Polytechnique Fédérale de Lausanne</string>\n" +
            "                <key>PayloadIdentifier</key>\n" +
            "                <string>org.pocketcampus.epflmailconfig</string>\n" +
            "            </dict>\n" +
            "        </array>\n" +
            "    </dict>\n" +
            "</plist>";

    public static final String VPN_XML_EN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple Inc//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "    <dict>\n" +
            "        <key>PayloadVersion</key>\n" +
            "        <integer>1</integer>\n" +
            "        <key>PayloadUUID</key>\n" +
            "        <string>CONFIGURATION_PAYLOAD_UUID</string>\n" +
            "        <key>PayloadType</key>\n" +
            "        <string>Configuration</string>\n" +
            "        <key>PayloadIdentifier</key>\n" +
            "        <string>org.pocketcampus.epflvpnconfig</string>\n" +
            "        <key>PayloadDisplayName</key>\n" +
            "        <string>EPFL VPN</string>\n" +
            "        <key>PayloadDescription</key>\n" +
            "        <string>Installs EPFL's VPN</string>\n" +
            "        <key>PayloadOrganization</key>\n" +
            "        <string>École Polytechnique Fédérale de Lausanne</string>\n" +
            "        <key>PayloadContent</key>\n" +
            "        <array>\n" +
            "            <dict>\n" +
            "\n" +
            "                <key>UserDefinedName</key>\n" +
            "                <string>EPFL VPN</string>\n" +
            "                <key>OverridePrimary</key>\n" +
            "                <true/>\n" +
            "                <key>VPNType</key>\n" +
            "                <string>L2TP</string>\n" +
            "\n" +
            "                <key>PPP</key>\n" +
            "                <dict>\n" +
            "                    <key>AuthName</key>\n" +
            "                    <string>USER_GASPAR</string>\n" +
            "                    <key>CommRemoteAddress</key>\n" +
            "                    <string>vpn-l2tp.epfl.ch</string>\n" +
            "                </dict>\n" +
            "\n" +
            "                <key>IPSec</key>\n" +
            "                <dict>\n" +
            "                    <key>AuthenticationMethod</key>\n" +
            "                    <string>SharedSecret</string>\n" +
            "                    <key>LocalIdentifierType</key>\n" +
            "                    <string>KeyID</string>\n" +
            "                    <key>SharedSecret</key>\n" +
            "                    <data>RVBGTC1MMlRQ</data>\n" +
            "                </dict>\n" +
            "\n" +
            "                <key>PayloadDescription</key>\n" +
            "                <string>Installs EPFL's VPN</string>\n" +
            "                <key>PayloadUUID</key>\n" +
            "                <string>ACCOUNT_PAYLOAD_UUID</string>\n" +
            "                <key>PayloadType</key>\n" +
            "                <string>com.apple.vpn.managed</string>\n" +
            "                <key>PayloadDisplayName</key>\n" +
            "                <string>EPFL</string>\n" +
            "                <key>PayloadVersion</key>\n" +
            "                <integer>1</integer>\n" +
            "                <key>PayloadOrganization</key>\n" +
            "                <string>École Polytechnique Fédérale de Lausanne</string>\n" +
            "                <key>PayloadIdentifier</key>\n" +
            "                <string>org.pocketcampus.epflvpnconfig</string>\n" +
            "                \n" +
            "            </dict>\n" +
            "        </array>\n" +
            "    </dict>\n" +
            "</plist>";

    public static final String VPN_XML_FR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple Inc//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "    <dict>\n" +
            "        <key>PayloadVersion</key>\n" +
            "        <integer>1</integer>\n" +
            "        <key>PayloadUUID</key>\n" +
            "        <string>CONFIGURATION_PAYLOAD_UUID</string>\n" +
            "        <key>PayloadType</key>\n" +
            "        <string>Configuration</string>\n" +
            "        <key>PayloadIdentifier</key>\n" +
            "        <string>org.pocketcampus.epflvpnconfig</string>\n" +
            "        <key>PayloadDisplayName</key>\n" +
            "        <string>VPN EPFL</string>\n" +
            "        <key>PayloadDescription</key>\n" +
            "        <string>Installe le VPN EPFL</string>\n" +
            "        <key>PayloadOrganization</key>\n" +
            "        <string>École Polytechnique Fédérale de Lausanne</string>\n" +
            "        <key>PayloadContent</key>\n" +
            "        <array>\n" +
            "            <dict>\n" +
            "\n" +
            "                <key>UserDefinedName</key>\n" +
            "                <string>EPFL VPN</string>\n" +
            "                <key>OverridePrimary</key>\n" +
            "                <true/>\n" +
            "                <key>VPNType</key>\n" +
            "                <string>L2TP</string>\n" +
            "\n" +
            "                <key>PPP</key>\n" +
            "                <dict>\n" +
            "                    <key>AuthName</key>\n" +
            "                    <string>USER_GASPAR</string>\n" +
            "                    <key>CommRemoteAddress</key>\n" +
            "                    <string>vpn-l2tp.epfl.ch</string>\n" +
            "                </dict>\n" +
            "\n" +
            "                <key>IPSec</key>\n" +
            "                <dict>\n" +
            "                    <key>AuthenticationMethod</key>\n" +
            "                    <string>SharedSecret</string>\n" +
            "                    <key>LocalIdentifierType</key>\n" +
            "                    <string>KeyID</string>\n" +
            "                    <key>SharedSecret</key>\n" +
            "                    <data>RVBGTC1MMlRQ</data>\n" +
            "                </dict>\n" +
            "\n" +
            "                <key>PayloadDescription</key>\n" +
            "                <string>Installs EPFL's VPN</string>\n" +
            "                <key>PayloadUUID</key>\n" +
            "                <string>ACCOUNT_PAYLOAD_UUID</string>\n" +
            "                <key>PayloadType</key>\n" +
            "                <string>com.apple.vpn.managed</string>\n" +
            "                <key>PayloadDisplayName</key>\n" +
            "                <string>EPFL</string>\n" +
            "                <key>PayloadVersion</key>\n" +
            "                <integer>1</integer>\n" +
            "                <key>PayloadOrganization</key>\n" +
            "                <string>École Polytechnique Fédérale de Lausanne</string>\n" +
            "                <key>PayloadIdentifier</key>\n" +
            "                <string>org.pocketcampus.epflvpnconfig</string>\n" +
            "                \n" +
            "            </dict>\n" +
            "        </array>\n" +
            "    </dict>\n" +
            "</plist>";


        public static void sign(String pemFile, String xml, OutputStream out) throws IOException {
                //openssl smime -sign -signer file.pem -inkey file.pem -certfile file.pem -nodetach -outform der
                Process proc = Runtime.getRuntime().exec(new String[]{
                        "openssl", "smime", "-sign",
                        "-signer", pemFile,
                        "-inkey", pemFile,
                        "-certfile", pemFile,
                        "-nodetach",
                        "-outform", "der"});
                proc.getOutputStream().write(xml.getBytes("UTF-8"));
                proc.getOutputStream().close();
                IOUtils.copy(proc.getInputStream(), out);

                //String status = IOUtils.toString(proc.getInputStream(), "UTF-8");

        }
}
