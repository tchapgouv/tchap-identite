<#import "template.ftl" as layout>
<@layout.registrationLayout ; section>
<#assign clientUrl = (client??)?then(client.baseUrl,'')>
<#assign clientName = (client??)?then(client.name,'')>

    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
    <#elseif section = "form">
        <h2>Authentification</h2>
        <div class="fr-callout fr-alert fr-alert--warning">

            <p class="fr-callout__text">${msg("error.unknown.domain","clientName")} </p>

            <a class="fr-btn fr-btn--primary" title="Nous contacter" href="https://audioconf.numerique.gouv.fr/contact">Nous contacter</a>


        </div>
        <a type="submit" class="fr-btn" href="${clientUrl}">Réessayer</a>
    </#if>
</@layout.registrationLayout>
