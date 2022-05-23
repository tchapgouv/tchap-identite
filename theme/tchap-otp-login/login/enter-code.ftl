<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo pageTitle="AudioConf"; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
    <#elseif section = "form">
        <h2>Rentrez votre code secret</h2>
        <form id="kc-form-login" class="${properties.kcFormClass!}" onsubmit="login.disabled = true; return true;"
              action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="codeInput" class="${properties.kcLabelClass!}">Renseignez le code que vous avez reçu sur <span style="text-decoration: underline;">Tchap</span>
                        et à votre adresse mail <strong>${userEmail!}</strong>
                        </label>
                </div>

                <div class="${properties.kcInputWrapperClass!}">
                    <input tabindex="1" id="codeInput" class="${properties.kcInputClass!} code-input" name="codeInput" type="text"
                           autofocus autocomplete="off" minlength="6" maxlength="7" required pattern="[a-z]{3}-?[a-z]{3}" placeholder=""/>
                </div>
            </div>

            <div class="${properties.kcLabelWrapperClass!}">
                <label for="email" class="${properties.kcLabelClass!}">Je n'ai pas reçu le code. <a href="">Me renvoyer le code</a></label>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <div class="${properties.kcFormButtonsWrapperClass!}">
                        <input tabindex="4"
                               class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                               name="login" id="login" type="submit" value="Confirmer"/>
                    </div>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
