# Windows Duck In The Middle

<a href="https://payloadstudio.hak5.org/community/?device=usb-rubber-ducky&viewurl=https://raw.githubusercontent.com/Who-Is-Julien/Ducky-Utilities/main/Payloads/Windows-Duck-In-The-Middle/payload.txt"><img alt="View on Payload Studio" src="https://img.shields.io/badge/View_on-Payload_Studio-red?style=flat-square"></a>

This payload configures the target PC user to redirect network traffic via a proxy, enabling a [Man-in-the-middle attack](https://en.wikipedia.org/wiki/Man-in-the-middle_attack). After executing your payload, the proxy server will intercept all the target user's network traffic.

<details>
<summary>Payload operation</summary>

1. Detects when the USB Rubber Ducky is ready and whether the target operating system is Windows.
2. Creates a new virtual desktop.
3. Opens a PowerShell window using the Windows+X menu.
4. Runs PowerShell code that performs the following actions:
- Downloads your certificate to a temporary file.
- *Configures Firefox to accepts root user certificates for each profile.*
- Configures and activates the proxy for the current user.
- Deletes the temporary certificate file and PowerShell history, then closes the window.
5. Confirms the addition of a trusted certificate in the confirmation dialog box.
6. Closes the virtual desktop.
7. *Disables USB Rubber Ducky*

> Note: No configuration is required for Edge and Chrome browsers, as they accept user root certificates by default.

</details>


## Prerequisites

To use this payload, you'll need a proxy server and a [root certificate](https://en.wikipedia.org/wiki/Root_certificate).
The certificate must be downloadable from a website, either from your proxy server or from an online file hosting service such as [Dropbox](https://www.dropbox.com). You can easily generate the certificate using tools such as [mitmproxy](https://mitmproxy.org) or [Burp Suite](https://portswigger.net/burp).

> Note: To ensure the payload functions properly, generate the "mitmproxy-ca-cert.pem" certificate in the "Other platforms" section when using mitmproxy.


## Options

|Required options|Data type|Default value|Description|
|-|-|-|-|
|CERT_URL|String|example.com|The download link for your Trusted Root CA certificate|
|PROXY_IP|String|127.0.0.1|Your proxy's IP address|
|PROXY_PORT|Integer|8080|Your proxy port|

|Advanced options|Data type|Default value|Description|
|-|-|-|-|
|SHORT_DELAY|Integer|500|Short delay time|
|MEDIUM_DELAY|Integer|2000|Medium delay time|
|LONG_DELAY|Integer|4000|Long delay time|
|CONFIGURE_FIREFOX|Boolean|TRUE|Configures Firefox to accepts root user certificates for each profile|
|DISABLE_AFTER_EXECUTION|Boolean|TRUE|Disables USB Rubber Ducky after payload execution|


## Authors

- [@Who-Is-Julien](https://github.com/Who-Is-Julien)
