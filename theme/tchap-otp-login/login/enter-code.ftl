<#import "template.ftl" as layout>
<@layout.registrationLayout pageTitle="${client.name}" ; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
    <#elseif section = "form">

        <h2>Renseignez le code que vous avez reçu</h2>
        <form id="kc-form-login" class="${properties.kcFormClass!}" onsubmit="login.disabled = true; return true;"
              action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                                        <label for="codeInput" class="${properties.kcLabelClass!}">Vous avez reçu le code  
                        à votre adresse mail <strong>${userEmail!}</strong> et sur <a  href="https://www.tchap.gouv.fr/" target="_blank">Tchap</a>. 
                                                ${client.name} utilise un service d'authentification pour assurer que seuls les agents publics utilisent les services qui
                        leur sont réservés.
                        </label>

                    <input tabindex="1" id="codeInput" class="${properties.kcInputClass!} code-input" name="codeInput" type="text"
                           autofocus minlength="6" maxlength="8" required ${(message?has_content && message.type = 'error' && errorType = 'error.email.not.sent')?then("disabled","")}/>
            </div>


<#--              <div class="${properties.kcFormGroupClass!}">
                <label for="email" class="${properties.kcLabelClass!}">Je n'ai pas reçu le code. <a href="/">Me renvoyer un code</a></label>
            </div>  -->


            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                        <input tabindex="4"
                               class="${properties.kcButtonClass!}"
                               name="login" id="login" type="submit" value="Confirmer"/>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
