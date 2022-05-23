<html>
<head>
    <meta charset="UTF-8" />
    <title>AudioConf</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width"/>
    </style>
</head>
<body>
<table class="body-wrap">
    <tr>
        <td class="container">
            <table>
                <tr>
                    <td class="content">

                        <p>${kcSanitize("Bonjour,")?no_esc}</p>
                        <p>${kcSanitize("voici votre code secret pour compléter votre réservation sur AudioConf")?no_esc}</p>

                        <p>${loginCode}</p>
<#--
                        <p>${kcSanitize(msg("loginCodeInstruction2"))?no_esc}</p>
-->
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
