import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver

import com.kms.katalon.core.annotation.SetUp
import com.kms.katalon.core.annotation.TearDown
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.comment('Story: [ON] As a Recruiter, I want to update questions added in a draft test.')

@SetUp()
def setUp() {
	WebUI.comment('Given I am logged into the system and there is at least one draft test with at least one question added,')
	
	'Login'
	WebUI.comment('Log in system')
	WebUI.callTestCase(findTestCase('Common Test Cases/Login/Login'), null)
	
	WebUI.comment('Click Tests on the menu')
	WebUI.waitForElementPresent(findTestObject('Object Repository/Module_Navigation/nav_item_tests'), 10)
	WebUI.click(findTestObject('Object Repository/Module_Navigation/nav_item_tests'))
	
	'Create draft test'	
	WebUI.callTestCase(findTestCase('Test Cases/Common Test Cases/Test/Add Test'), null)
	WebUI.waitForElementPresent(findTestObject('Page_TestOverview/toast_Successfully'), 10)
	WebUI.click(findTestObject('Page_TestOverview/toast_Successfully'))
	
	'Click questions in the left sidebar'
	WebUI.waitForElementPresent(findTestObject('Page_TestQuestions/btn_TestQuestion'), 10)
	WebUI.click(findTestObject('Page_TestQuestions/btn_TestQuestion'))
	
	'Create MCQ Question.'
	WebUI.callTestCase(findTestCase('Common Test Cases/Test/Create MCQ'), null)
}

@TearDown()
def tearDown() {
	'Delete the test'	
	WebUI.waitForElementPresent(findTestObject('Object Repository/Page_TestOverview/txt_TestName'), 30)
	testName = WebUI.getText(findTestObject('Object Repository/Page_TestOverview/txt_TestName'))
	
	WebUI.callTestCase(findTestCase('Test Cases/Common Test Cases/Test/Tear Down Test'), ['testName': testName])
}

WebUI.comment('Click on a question')
WebUI.waitForElementPresent(findTestObject('Page_TestQuestions/btn_Question__relyOnOrder', [('order') : 1]), 10)
WebUI.click(findTestObject('Page_TestQuestions/btn_Question__relyOnOrder', [('order') : 1]))

WebUI.comment('Verify system show a popup "Update question"')
WebUI.waitForElementPresent(findTestObject('Page_TestQuestions/dialog_Dialog'), 10)

WebUI.takeScreenshotAsCheckpoint("Update MCQ in draft Test")

WebUI.comment('Enter valid values')
setUpdateValues()

WebUI.comment('Click Save')
WebUI.click(findTestObject('Page_TestQuestions/btn_SubmitButton'))

WebUI.comment('Verify changes, show a successful message and redirect to Questions listing page of that test.')
verify()

def setUpdateValues() {
	WebUI.clearText(findTestObject('Page_TestQuestions/txt_ProblemStatement'))
	
	WebUI.sendKeys(findTestObject('Page_TestQuestions/ckedior_ProblemStatement'), statement)
	
	WebUI.click(findTestObject('Page_TestQuestions/label_MultipleChoice', [('CHOOSE_TYPE') : chooseType]))
	
	List answerTextbox = WebUI.findWebElements(findTestObject('Page_TestQuestions/tbx_Answer'), 2)
	
	for (int i = 0; i < answers.size(); i++) {
		if (i >= answerTextbox.size()) {
			WebUI.click(findTestObject('Object Repository/Page_TestQuestions/btn_AddChoice'))
	
			answerTextbox = WebUI.findWebElements(findTestObject('Page_TestQuestions/txt_Answers'), 2)
		}
		
		answerTextbox.get(i).clear()
		answerTextbox.get(i).sendKeys(answers[i])
	}
	
	WebUI.click(findTestObject('Page_TestQuestions/cbx_rightAnswer'))
	
	WebUI.selectOptionByValue(findTestObject('Page_TestQuestions/select_Difficulty'), difficulty, false)
	
	WebUI.setText(findTestObject('Page_TestQuestions/tbx_AwardedScore'), awardedScore)
	
	WebUI.setText(findTestObject('Page_TestQuestions/tbx_SubtractedScore'), subtractedScore)
	
	WebUI.setText(findTestObject('Page_TestQuestions/tbx_MaxTime'), maxTime)
	
	for (def tag : tags) {
		WebUI.sendKeys(findTestObject('Page_TestQuestions/tbx_TagsInput'), tag)
	
		WebUI.sendKeys(findTestObject('Page_TestQuestions/tbx_TagsInput'), Keys.chord(Keys.ENTER))
	}
}

def verify() {
	WebUI.waitForElementPresent(findTestObject('Page_TestQuestions/toast_Successfully'), 10)
	
	WebUI.verifyElementText(findTestObject('Page_TestQuestions/toast_Successfully'), 'Update question successfully.')
}

