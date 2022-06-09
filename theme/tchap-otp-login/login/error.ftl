<#import "template.ftl" as layout>
<!-- error page miss the client variable sometimes-->
<#assign clientUrl = (client??)?then(client.baseUrl,'')>

<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
    <#elseif section = "form">
        <h2>Authentification</h2>

                <div class="fr-callout fr-alert fr-alert--error">

                    <p class="fr-callout__text">${kcSanitize(message.summary)?no_esc}</p>

                    <a class="fr-btn fr-btn--primary" title="Nous contacter" href="https://audioconf.numerique.gouv.fr/contact">Nous contacter</a>


                </div>
                    <a type="submit" class="fr-btn" href="${clientUrl}">Réessayer</a>


    </#if>
</@layout.registrationLayout>