import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.annotation.SetUp
import com.kms.katalon.core.annotation.TearDown
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.database.Database as DB
import generator.DynamicGenerator
import groovy.transform.Field

@Field String validEmail = "katalontestingontest@gmail.com"
@Field String validName = "Hoa Pham"
@Field String password = DynamicGenerator.getInstance().getDynamicVariableByClassName(getClass()) + "@a1"

@SetUp()
def setUp( ) {	
	WebDriver driver = DriverFactory.getWebDriver()
	
	'Login'
	WebUI.callTestCase(findTestCase('Test Cases/Common Test Cases/Login/Login'),null)
	'Connect DB'
	DB.connect(GlobalVariable.G_DB_HOST, GlobalVariable.G_DB_NAME, GlobalVariable.G_DB_PORT, GlobalVariable.G_DB_USERNAME,
		GlobalVariable.G_DB_PASSWORD)
}
@TearDown()
def tearDown() {
	'delete user'
	WebUI.callTestCase(findTestCase('Test Cases/Common Test Cases/Members/Tear Down Member'), ['email': validEmail ])
}

def getSetPasswordUrl() {
	def url = StringBuilder.newInstance()
	url<<GlobalVariable.G_SiteURL
	url<<"/create-password?token="
	def query = "SELECT reset_password_token from \"user\" where email = '${validEmail}';"
	def userToken = DB.execute(query).get(0).get(0)
	url<<"${userToken}"
	println(url)
	return url.toString()
}


WebUI.click(findTestObject('Object Repository/Module_Navigation/nav_Members'))

WebUI.click(findTestObject('Object Repository/Page_Create Member/btn_Create Member'))

WebUI.setText(findTestObject('Object Repository/Page_Create Member/inp_Email'), validEmail)

WebUI.setText(findTestObject('Object Repository/Page_Create Member/inp_Full Name'), validName)

WebUI.click(findTestObject('Object Repository/Page_Create Member/select_Role'))

WebUI.click(findTestObject('Object Repository/Page_Create Member/txt_Role',['role':'Super Admin']))

WebUI.comment('Delete all email')
CustomKeywords.'com.testwithhari.katalon.plugins.Gmail.deleteAllEMails'(validEmail, '0865800354', 'Inbox')

WebUI.click(findTestObject('Object Repository/Page_Create Member/btn_modal_add'))

def givenMessage = WebUI.getText(findTestObject('Object Repository/Module_Navigation/toast_Success'))
WebUI.verifyMatch(givenMessage, 'Create member successfully.', false)

def numberOfEmail = CustomKeywords.'com.testwithhari.katalon.plugins.Gmail.getEmailsCount'(validEmail, '0865800354', 'Inbox')
WebUI.verifyMatch(numberOfEmail.toString(), '1', false, FailureHandling.STOP_ON_FAILURE)

def url = getSetPasswordUrl()
WebUI.openBrowser(url)
WebUI.waitForPageLoad(15, FailureHandling.CONTINUE_ON_FAILURE)

WebUI.setText(findTestObject('Object Repository/Page_Set Password/txt_Password'), password)
WebUI.setText(findTestObject('Object Repository/Page_Set Password/txt_Repassword'), password)
WebUI.click(findTestObject('Object Repository/Page_Set Password/btn_Set Password'))

WebUI.callTestCase(findTestCase('Test Cases/Common Test Cases/Login/Login With Password And Username'), ['username':validEmail,'password':password])