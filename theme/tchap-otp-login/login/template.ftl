<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false displayWide=false showAnotherWayIfPresent=true clientUrl="" pageTitle="">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">

    <#if properties.meta?has_content>
        <#list properties.meta?split(' ') as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
        </#list>
    </#if>
    <title>${pageTitle}</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" />
    <link rel="icon" type="image/png" sizes="16x16" href="${url.resourcesPath}/img/favicon-16x16.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${url.resourcesPath}/img/favicon-32x32.png">
    <#if properties.stylesCommon?has_content>
        <#list properties.stylesCommon?split(' ') as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
        </#list>
    </#if>
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>
</head>

<body>
<header role="banner" class="fr-header">
    <div class="fr-header__body">
        <div class="fr-container">
            <div class="fr-header__body-row">
                <div class="fr-header__brand fr-enlarge-link">
                    <div class="fr-header__brand-top">
                        <div class="fr-header__logo">
                            <p class="fr-logo">
                                République
                                <br>Française
                            </p>
                        </div>
                        <div class="fr-header__navbar">
                            <button class="fr-btn--menu fr-btn" data-fr-opened="false" aria-controls="modal-menu" aria-haspopup="menu" title="Menu">
                                Menu
                            </button>
                        </div>
                    </div>
                    <div class="fr-header__service">
                        <a href="${clientUrl}" title="Accueil - AudioConf">
                            <p class="fr-header__service-title">
                                ${pageTitle}
                            </p>
                        <p class="fr-header__service-tagline">Conférences téléphoniques pour les agents de l'État</p>
                        </a>
                    </div>
                </div>
                <div class="fr-header__tools">
                    <div class="fr-header__tools-links">

                        <#--<ul class="fr-links-group">
                            <li class="fr-link fr-fi-question-line">
                                <a href="/questions-frequentes">Une question ?</a>
                            </li>
                            <li>
                                <button class="fr-link fr-fi-theme-fill fr-link--icon-left" aria-controls="fr-theme-modal" data-fr-opened="false">Paramètres d'affichage</button>                </li>
                        </ul>-->
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Navigation principale -->
    <div class="fr-header__menu fr-modal" id="modal-menu" aria-labelledby="button-834">
        <div class="fr-container">
            <button class="fr-link--close fr-link" aria-controls="modal-menu">Fermer</button>
            <div class="fr-header__menu-links"></div>
        </div>
    </div>
</header>

<main role="main" id="main" class="fr-container" >
  <div class="fr-container  fr-mt-4w fr-mb-4w">
    <div class="fr-grid-row fr-grid-row--center">
      <div class="fr-col-xs-12 fr-col-sm-8 fr-col-md-6 fr-col-lg-8">

          <!--p>Domaine ${kcSanitize(msg("loginTitleHtml",(realm.displayNameHtml!'')))?no_esc}</p-->

              <#if realm.internationalizationEnabled  && locale.supported?size gt 1>
                  <div id="kc-locale">
                      <div id="kc-locale-wrapper" class="${properties.kcLocaleWrapperClass!}">
                          <div class="kc-dropdown" id="kc-locale-dropdown">
                              <a href="#" id="kc-current-locale-link">${locale.current}</a>
                              <ul>
                                  <#list locale.supported as l>
                                      <li class="kc-dropdown-item"><a href="${l.url}">${l.label}</a></li>
                                  </#list>
                              </ul>
                          </div>
                      </div>
                  </div>
              </#if>
              <#if !(auth?has_content && auth.showUsername() && !auth.showResetCredentials())>
                  <#if displayRequiredFields>
                      <div class="${properties.kcContentWrapperClass!}">
                          <div class="${properties.kcLabelWrapperClass!} subtitle">
                              <span class="subtitle"><span class="required">*</span> ${msg("requiredFields")}</span>
                          </div>
                          <div class="col-md-10">
                              <h1 id="kc-page-title"><#nested "header"></h1>
                          </div>
                      </div>
                  <#else>
                      <h1 id="kc-page-title"><#nested "header"></h1>
                  </#if>
              <#else>
                  <#if displayRequiredFields>
                      <div class="${properties.kcContentWrapperClass!}">
                          <div class="${properties.kcLabelWrapperClass!} subtitle">
                              <span class="subtitle"><span class="required">*</span> ${msg("requiredFields")}</span>
                          </div>
                          <div class="col-md-10">
                              <#nested "show-username">
                              <div class="${properties.kcFormGroupClass!}">
                                  <div id="kc-username">
                                      <label id="kc-attempted-username">${auth.attemptedUsername}</label>
                                      <a id="reset-login" href="${url.loginRestartFlowUrl}">
                                          <div class="kc-login-tooltip">
                                              <i class="${properties.kcResetFlowIcon!}"></i>
                                              <span class="kc-tooltip-text">${msg("restartLoginTooltip")}</span>
                                          </div>
                                      </a>
                                  </div>
                              </div>
                          </div>
                      </div>
                  <#else>
                      <#nested "show-username">
                      <div class="${properties.kcFormGroupClass!}">
                          <div id="kc-username">
                              <label id="kc-attempted-username">${auth.attemptedUsername}</label>
                              <a id="reset-login" href="${url.loginRestartFlowUrl}">
                                  <div class="kc-login-tooltip">
                                      <i class="${properties.kcResetFlowIcon!}"></i>
                                      <span class="kc-tooltip-text">${msg("restartLoginTooltip")}</span>
                                  </div>
                              </a>
                          </div>
                      </div>
                  </#if>
              </#if>

            <div id="kc-content">
            <div id="kc-content-wrapper">
              <#-- App-initiated actions should not see warning messages about the need to complete the action -->
              <#-- during login.                                                                               -->
              <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                  <div class="fr-alert fr-alert--${message.type} fr-alert--sm">
                      <#if message.type = 'success'><span class="${properties.kcFeedbackSuccessIcon!}"></span></#if>
                      <#if message.type = 'warning'><span class="${properties.kcFeedbackWarningIcon!}"></span></#if>
                      <#if message.type = 'error'><span class="${properties.kcFeedbackErrorIcon!}"></span></#if>
                      <#if message.type = 'info'><span class="${properties.kcFeedbackInfoIcon!}"></span></#if>
                      <p>${kcSanitize(message.summary)?no_esc}</p>
                  </div>
              </#if>

              <#nested "form">

              <#if auth?has_content && auth.showTryAnotherWayLink() && showAnotherWayIfPresent>
              <form id="kc-select-try-another-way-form" action="${url.loginAction}" method="post" <#if displayWide>class="${properties.kcContentWrapperClass!}"</#if>>
                  <div class="${properties.kcFormGroupClass!}">
                    <input type="hidden" name="tryAnotherWay" value="on" />
                    <a href="#" id="try-another-way" class="fr-link" onclick="document.forms['kc-select-try-another-way-form'].submit();return false;">${msg("doTryAnotherWay")}</a>
                  </div>
              </form>
              </#if>

              <#if displayInfo>
                  <div id="kc-info">
                      <div id="kc-info-wrapper">
                          <#nested "info">
                      </div>
                  </div>
              </#if>
            </div>
          </div>
          </div>

      </div>
    </div>
  </div>
</main>
<footer class="fr-footer" role="contentinfo">
    <div class="fr-container">
        <div class="fr-footer__body">
            <div class="fr-footer__brand">
                <span class="fr-logo fr-logo__title">république<br>française</span>
            </div>
            <div class="fr-footer__content">
                <p class="fr-footer__content-desc">
                    Service proposé par la Direction interministérielle du numérique et la Direction du numérique pour l'éducation
                </p>

                <ul class="fr-footer__content-links">
                    <li>
                        <a href="https://legifrance.gouv.fr">legifrance.gouv.fr</a>
                    </li>
                    <li>
                        <a href="https://gouvernement.fr">gouvernement.fr</a>
                    </li>
                    <li>
                        <a href="https://service-public.fr">service-public.fr</a>
                    </li>
                    <li>
                        <a href="https://data.gouv.fr">data.gouv.fr</a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fr-footer__bottom">
            <ul class="fr-footer__bottom-list">
                <li>
                    <a href="${properties.kcExternalFooterLinksBaseUrl!}/accessibilite">Accessibilité : non conforme</a>                </li>
                <li>
                    <a href="${properties.kcExternalFooterLinksBaseUrl!}/mentions_legales">Mentions légales</a>
                </li>
                <li>
                    <a href="${properties.kcExternalFooterLinksBaseUrl!}/donnees_personnelles">Données personnelles et cookies</a>
                </li>
                <li>
                    <a href="${properties.kcExternalFooterLinksBaseUrl!}/cgu">Conditions générales d’utilisation</a>
                </li>
            </ul>
        </div>
    </div>
</footer>
</body>
</html>
</#macro>
