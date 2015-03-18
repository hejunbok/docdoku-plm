/*global casper,urls,products*/

casper.test.begin('Parts  multiple checkout tests suite', 1, function partsMultipleCheckoutTestsSuite(){
    'use strict';

    casper.open('');

    /**
    * Open product management URL
    * */

    casper.then(function(){
        this.open(urls.productManagement);
    });

    /**
     * Go to part nav
     */
    casper.then(function waitForPartNavLink(){
        this.waitForSelector('#part-nav > .nav-list-entry > a',function clickPartNavLink() {
            this.click('#part-nav > .nav-list-entry > a');
        },function fail(){
            this.capture('screenshot/partCheckout/waitForPartNavLink-error.png');
            this.test.assert(false,'Part nav link can not be found');
        });
    });

    /**
     * Select the parts with checkbox
     */

    casper.then(function waitForPartTable(){
        this.waitForSelector('#part_table tbody tr:nth-child(2)  td:first-child input',function clickOnPartCheckbox() {
            this.click('#part_table tbody tr:nth-child(2)  td:first-child input');
        },function fail(){
            this.capture('screenshot/partsCheckout/waitForPartTable-error.png');
            this.test.assert(false,'Part cannot be found');
        });
    });
    casper.then(function waitForPartTable(){
        this.waitForSelector('#part_table tbody tr:nth-child(3)  td:first-child input',function clickOnPartCheckbox() {
            this.click('#part_table tbody tr:nth-child(3)  td:first-child input');
        },function fail(){
            this.capture('screenshot/partsCheckout/waitForPartTable-error.png');
            this.test.assert(false,'Part cannot be found');
        });
    });
     casper.then(function waitForPartTable(){
        this.waitForSelector('#part_table tbody tr:nth-child(5)  td:first-child input',function clickOnPartCheckbox() {
            this.click('#part_table tbody tr:nth-child(5)  td:first-child input');
        },function fail(){
            this.capture('screenshot/partsCheckout/waitForPartTable-error.png');
            this.test.assert(false,'Part cannot be found');
        });
    });


    /**
     * Click on checkout button
     */
    casper.then(function waitForCheckoutButton(){
        this.waitForSelector('.actions .checkout',function clickOnCheckoutButton() {
            this.click('.actions .checkout');
        },function fail() {
            this.capture('screenshot/partCheckout/waitForCheckoutButton-error.png');
            this.test.assert(false,'Checkout button can not be found');
        });
    });

    /**
     * Wait for the checkout button to be disabled
     */
    casper.then(function waitForCheckoutButtonDisabled(){
        this.waitForSelector('.actions .checkout:disabled',function partIsCheckout() {
            this.test.assert(true,'Parts have been checkout');
        },function fail() {
            this.capture('screenshot/baselineCreation/waitForCheckoutButtonDisabled-error.png');
            this.test.assert(false,'Parts have not been checkout');
        });
    });


    casper.run(function allDone() {
        this.test.done();
    });

});