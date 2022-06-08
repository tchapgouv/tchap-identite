<#import "template.ftl" as layout>
<@layout.registrationLayout ; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
    <#elseif section = "form">
        <h2>Réserver une conférence téléphonique</h2>
        <div class="fr-callout fr-alert fr-alert--warning">

            <p class="fr-callout__text">Cet email ne correspond pas à une agence de l'État. Si vous appartenez à un service de l'État mais votre email n'est pas reconnu par AudioConf, contactez-nous pour que nous le rajoutions!</p>

            <a class="fr-btn fr-btn--primary" title="Nous contacter" href="https://audioconf.numerique.gouv.fr/contact">Nous contacter</a>


        </div>
        <a type="submit" class="fr-btn" href="${client.baseUrl}">Réserver une autre conférence</a>
    </#if>
</@layout.registrationLayout>
