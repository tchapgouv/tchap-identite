<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo pageTitle="${client.name}" clientUrl="${client.baseUrl}" ; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
    <#elseif section = "form">
        <h2>Rentrez votre code secret</h2>
        <form id="kc-form-login" class="${properties.kcFormClass!}" onsubmit="login.disabled = true; return true;"
              action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                    <label for="codeInput" class="${properties.kcLabelClass!}">Renseignez le code que vous avez reçu à votre adresse mail <strong>${userEmail!}</strong>
                        </label>

                    <input tabindex="1" id="codeInput" class="${properties.kcInputClass!} code-input" name="codeInput" type="text"
                           autofocus autocomplete="off" minlength="6" maxlength="7" required pattern="[a-z]{3}-?[a-z]{3}" placeholder=""/>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <label for="email" class="${properties.kcLabelClass!}">Je n'ai pas reçu le code. <a href="">Me renvoyer le code</a></label>
            </div>

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
