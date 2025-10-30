import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

// Создание учётки admin
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount('admin', 'admin')  // логин: admin, пароль: admin
instance.setSecurityRealm(hudsonRealm)

// Включение простой аутентификации
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
instance.setAuthorizationStrategy(strategy)
instance.save()