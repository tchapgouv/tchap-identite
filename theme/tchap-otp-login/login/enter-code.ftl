<#import "template.ftl" as layout>
<@layout.registrationLayout pageTitle="${client.name}" ; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
    <#elseif section = "form">
    <form id="kc-form-login" class="" onsubmit="login.disabled = true; return true;"
              action="${url.loginAction}" method="post">
        <!-- container without borders -->
        <div class="fr-container--fluid">
            <div class="fr-grid-row">
                <div class="fr-col">
                    <h2>Confirmez votre réservation</h2>
                </div>
            </div>
            <div class="fr-grid-row">
             <label for="codeInput" class="fr-label">
                    Renseignez le code d'authentification que vous avez reçu à votre adresse mail <strong>${userEmail!}</strong><#if feature_tchap_bot> et sur <a href="https://www.tchap.gouv.fr/" target="_blank">Tchap</a></#if>.
                    <#--  ${client.name} utilise un service d'authentification pour assurer que seuls les agents publics
                    utilisent les services qui leur sont réservés.  -->
                </label>
            </div>
            <div class="fr-grid-row fr-grid-row--middle">
                <div class="fr-col-3 hideMobile">
                    <img src="${url.resourcesPath}/img/enter-code-envelop.png" class="fr-responsive-img fr-p-4w"/>
                </div>
                <div class="fr-col-1 hideMobile">
                    <img src="${url.resourcesPath}/img/enter-code-arrow.png" class="fr-responsive-img fr-p-1w"/>
                </div>
                <div class="fr-col-12 fr-col-md-6 fr-p-4w fr-mt-5w">
                    <div class="fr-grid-row">
                          <input tabindex="1" id="codeInput" class="fr-input codeInput fr-m-auto" name="codeInput" type="text" placeholder="abc-def" autofocus minlength="6" maxlength="8" required ${(message?has_content && message.type = 'error' && errorType = 'error.email.not.sent')?then("disabled","")}/>
                    </div>
                    <div class="fr-grid-row fr-my-3w">
                        <input tabindex="4" class="fr-btn fr-m-auto" name="login" id="login" type="submit" value="Confirmer"/>
                    </div>
                      <div class="fr-grid-row fr-grid-row--center">
                        <span>Je n’ai pas reçu de code. <a href="">Me renvoyer le code </a> </span>  
                    </div>
                </div>
                <div class="fr-col-offset-md-2">
                </div>
            </div>
        </div>
    </form>
    </#if>
</@layout.registrationLayout>
