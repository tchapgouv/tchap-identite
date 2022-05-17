<html>
<head>
<meta charset="UTF-8" />
          <title>Intension code login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width"/>
    <style type="text/css">
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@300;400&amp;display=swap');
* { margin: 0; padding: 0; font-size: 100%; font-family: 'Roboto', Arial, Helvetica, sans-serif; line-height: 1.65; }
img { max-width: 100%; margin: 0 auto; display: block; }
body, .body-wrap { width: 100% !important; height: 100%; background: #69B0F6; }
button { color: #4582D9; text-decoration: none; }
.text-center { text-align: center; }
.text-right { text-align: right; }
.text-left { text-align: left; }
button { display: inline-block; color: black; background: #4582D9; border: solid #4582D9; border-width: 10px 20px 8px; font-weight: bold; border-radius: 10px; }
h2, h3 { margin-bottom: 10px; line-height: 1.25; }
h2 { font-size: 30px; }
h3 { font-size: 30px; }
p, ul, ol { font-size: 19px; font-weight: normal; margin-bottom: 17px; }
.container { display: block !important; clear: both !important; margin: 0 auto !important; max-width: 580px !important; }
.container table { width: 100% !important; border-collapse: collapse; }
.container .masthead { padding: 20px 0; background: #4582D9; color: white; }
.container .masthead h1 { margin: 0 auto !important; max-width: 90%; text-transform: ; }
.container .content { background: white; padding: 30px 35px; }
.container .content.footer { background: none; }
.container .content.footer p { margin-bottom: 0; color: #365D84; text-align: center; font-size: 14px; }
.container .content.footer a { color: #365D84; text-decoration: none; font-weight: bold; }
.container .content.footer a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<table class="body-wrap">
    <tr>
        <td class="container">
            <table>
                <tr>
                    <td class="content">
                        <div style="text-align:center">
                        <h2>${kcSanitize(msg("loginCodeTitle"))?no_esc}</h2>
                        <p>${kcSanitize(msg("loginCodeInstruction1"))?no_esc}</p>
                        <table>
                            <tr>
                                <td align="center">
                                    <h3>${kcSanitize(msg("loginCodeCode", loginCode))?no_esc}</h3>
                                </td>
                            </tr>
                        </table>
                        <p>${kcSanitize(msg("loginCodeInstruction2"))?no_esc}</p></div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>