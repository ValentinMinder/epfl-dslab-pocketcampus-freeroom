<?php

$EPFL_EMAIL_CONFIG_PLIST_EN = <<<XML
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple Inc//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
    <dict>
        <key>PayloadVersion</key>
        <integer>1</integer>
        <key>PayloadUUID</key>
        <string>CONFIGURATION_PAYLOAD_UUID</string>
        <key>PayloadType</key>
        <string>Configuration</string>
        <key>PayloadIdentifier</key>
        <string>org.pocketcampus.epflmailconfig</string>
        <key>PayloadDisplayName</key>
        <string>EPFL email</string>
        <key>PayloadDescription</key>
        <string>Installs your EPFL email</string>
        <key>PayloadOrganization</key>
        <string>École Polytechnique Fédérale de Lausanne</string>
        <key>PayloadContent</key>
        <array>
            <dict>

                <key>EmailAddress</key>
                <string>USER_EMAIL</string>
                <key>Host</key>
                <string>ewa.epfl.ch</string>
                <key>SSL</key>
                <true/>
                <key>UserName</key>
                <string>USER_GASPAR</string>
                <key>MailNumberOfPastDaysToSync</key>
                <integer>365</integer>
                
                <key>PayloadDescription</key>
                <string>Installs your EPFL email</string>
                <key>PayloadUUID</key>
                <string>ACCOUNT_PAYLOAD_UUID</string>
                <key>PayloadType</key>
                <string>com.apple.eas.account</string>
                <key>PayloadDisplayName</key>
                <string>EPFL email</string>
                <key>PayloadVersion</key>
                <integer>1</integer>
                <key>PayloadOrganization</key>
                <string>École Polytechnique Fédérale de Lausanne</string>
                <key>PayloadIdentifier</key>
                <string>org.pocketcampus.epflmailconfig</string>
                
            </dict>
        </array>
    </dict>
</plist>
XML;

$EPFL_EMAIL_CONFIG_PLIST_FR = <<<XML
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple Inc//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
    <dict>
        <key>PayloadVersion</key>
        <integer>1</integer>
        <key>PayloadUUID</key>
        <string>CONFIGURATION_PAYLOAD_UUID</string>
        <key>PayloadType</key>
        <string>Configuration</string>
        <key>PayloadIdentifier</key>
        <string>org.pocketcampus.epflmailconfig</string>
        <key>PayloadDisplayName</key>
        <string>Email EPFL</string>
        <key>PayloadDescription</key>
        <string>Installe votre compte email EPFL</string>
        <key>PayloadOrganization</key>
        <string>École Polytechnique Fédérale de Lausanne</string>
        <key>PayloadContent</key>
        <array>
            <dict>

                <key>EmailAddress</key>
                <string>USER_EMAIL</string>
                <key>Host</key>
                <string>ewa.epfl.ch</string>
                <key>SSL</key>
                <true/>
                <key>UserName</key>
                <string>USER_GASPAR</string>
                <key>MailNumberOfPastDaysToSync</key>
                <integer>365</integer>
                
                <key>PayloadDescription</key>
                <string>Installe votre compte email EPFL</string>
                <key>PayloadUUID</key>
                <string>ACCOUNT_PAYLOAD_UUID</string>
                <key>PayloadType</key>
                <string>com.apple.eas.account</string>
                <key>PayloadDisplayName</key>
                <string>Email EPFL</string>
                <key>PayloadVersion</key>
                <integer>1</integer>
                <key>PayloadOrganization</key>
                <string>École Polytechnique Fédérale de Lausanne</string>
                <key>PayloadIdentifier</key>
                <string>org.pocketcampus.epflmailconfig</string>
                
            </dict>
        </array>
    </dict>
</plist>
XML;

class UUID {
  public static function v3($namespace, $name) {
    if(!self::is_valid($namespace)) return false;

    // Get hexadecimal components of namespace
    $nhex = str_replace(array('-','{','}'), '', $namespace);

    // Binary Value
    $nstr = '';

    // Convert Namespace UUID to bits
    for($i = 0; $i < strlen($nhex); $i+=2) {
      $nstr .= chr(hexdec($nhex[$i].$nhex[$i+1]));
    }

    // Calculate hash value
    $hash = md5($nstr . $name);

    return sprintf('%08s-%04s-%04x-%04x-%12s',

      // 32 bits for "time_low"
      substr($hash, 0, 8),

      // 16 bits for "time_mid"
      substr($hash, 8, 4),

      // 16 bits for "time_hi_and_version",
      // four most significant bits holds version number 3
      (hexdec(substr($hash, 12, 4)) & 0x0fff) | 0x3000,

      // 16 bits, 8 bits for "clk_seq_hi_res",
      // 8 bits for "clk_seq_low",
      // two most significant bits holds zero and one for variant DCE1.1
      (hexdec(substr($hash, 16, 4)) & 0x3fff) | 0x8000,

      // 48 bits for "node"
      substr($hash, 20, 12)
    );
  }

  public static function v4() {
    return sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x',

      // 32 bits for "time_low"
      mt_rand(0, 0xffff), mt_rand(0, 0xffff),

      // 16 bits for "time_mid"
      mt_rand(0, 0xffff),

      // 16 bits for "time_hi_and_version",
      // four most significant bits holds version number 4
      mt_rand(0, 0x0fff) | 0x4000,

      // 16 bits, 8 bits for "clk_seq_hi_res",
      // 8 bits for "clk_seq_low",
      // two most significant bits holds zero and one for variant DCE1.1
      mt_rand(0, 0x3fff) | 0x8000,

      // 48 bits for "node"
      mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)
    );
  }

  public static function v5($namespace, $name) {
    if(!self::is_valid($namespace)) return false;

    // Get hexadecimal components of namespace
    $nhex = str_replace(array('-','{','}'), '', $namespace);

    // Binary Value
    $nstr = '';

    // Convert Namespace UUID to bits
    for($i = 0; $i < strlen($nhex); $i+=2) {
      $nstr .= chr(hexdec($nhex[$i].$nhex[$i+1]));
    }

    // Calculate hash value
    $hash = sha1($nstr . $name);

    return sprintf('%08s-%04s-%04x-%04x-%12s',

      // 32 bits for "time_low"
      substr($hash, 0, 8),

      // 16 bits for "time_mid"
      substr($hash, 8, 4),

      // 16 bits for "time_hi_and_version",
      // four most significant bits holds version number 5
      (hexdec(substr($hash, 12, 4)) & 0x0fff) | 0x5000,

      // 16 bits, 8 bits for "clk_seq_hi_res",
      // 8 bits for "clk_seq_low",
      // two most significant bits holds zero and one for variant DCE1.1
      (hexdec(substr($hash, 16, 4)) & 0x3fff) | 0x8000,

      // 48 bits for "node"
      substr($hash, 20, 12)
    );
  }

  public static function is_valid($uuid) {
    return preg_match('/^\{?[0-9a-f]{8}\-?[0-9a-f]{4}\-?[0-9a-f]{4}\-?'.
                      '[0-9a-f]{4}\-?[0-9a-f]{12}\}?$/i', $uuid) === 1;
  }
}


/*********
LOGIC
*/


if(!empty($_GET["config"]) && $_GET["config"] == "email") {

  if(empty($_GET["email"]) || empty($_GET["gaspar"])) {
    die("Sorry, you did not provide your username and email address");
  }
  $content = $EPFL_EMAIL_CONFIG_PLIST_EN;
  if(!empty($_GET["lang"]) && $_GET["lang"] == "fr") {
    $content = $EPFL_EMAIL_CONFIG_PLIST_FR;
  }

  $IOS_EPFL_EMAIL_CONFIG_UUID = "ca8ea362-d121-45c1-9db4-a4e64b80f90c";

  $configuration_payload_uuid = UUID::v3($IOS_EPFL_EMAIL_CONFIG_UUID, "{$_GET["email"]} {$_GET["gaspar"]} configuration payload uuid");
  $account_payload_uuid = UUID::v3($IOS_EPFL_EMAIL_CONFIG_UUID, "{$_GET["email"]} {$_GET["gaspar"]} account payload uuid");

  header("Content-Type: application/x-apple-aspen-config"); // ; charset=utf-8
  header('Content-Disposition: attachment; filename="EPFL_mail.mobileconfig"');

  $content = str_replace("USER_EMAIL", "{$_GET["email"]}", $content);
  $content = str_replace("USER_GASPAR", "{$_GET["gaspar"]}", $content);
  $content = str_replace("CONFIGURATION_PAYLOAD_UUID", "$configuration_payload_uuid", $content);
  $content = str_replace("ACCOUNT_PAYLOAD_UUID", "$account_payload_uuid", $content);

  echo "$content";

} else {
  die("Sorry, I don't know what you want to configure");
}

?>