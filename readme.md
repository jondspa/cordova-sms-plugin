# Cordova SMS Plugin

Plugin for sending Android MMS.  EXtension of cordova-sms-plugin.

         ##### UNDER DEVELOPMENT!!!  Use at your own risk.  @dbaq is the expert on the base of this plugin.

## Installing the plugin

    cordova plugin add https://github.com/jondspa/cordova-sms-plugin.git

## Using the plugin
HTML

    <input id="numberTxt" placeholder="Enter mobile number" value="" type="tel" />
    <textarea id="messageTxt" placeholder="Enter message"></textarea>
    <input type="button" onclick="app.sendSms()" value="Send SMS" />

Javascript
    
    To send MMS on Android, here is a snippet:
    
    img = document.createElement("IMG");
    img.src = filename;

    canvas = document.createElement("canvas");
    ctx = canvas.getContext("2d");
    canvas.width = img.width;
    canvas.height = img.height;
    ctx.fillStyle = "#000000";
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.drawImage(img, 0, 0);

    dataURL = canvas.toDataURL();
    
    number = document.getElementById('numberTxt').innerHTML;
    message = document.getElementById('messageTxt').innerHTML;
    intent = 'INTENT';
    if (dataURL === "" || dataURL === null) {
        sms.send(number, message, "", intent, success, error);
    } else {
        sms.send(number, message, dataURL, intent, success, error);
    }
    success = function () {
        if (DEBUG) {
            window.alert('Message sent successfully');
        }
    };
    error = function (e) {
        if (DEBUG) {
            window.alert('Message Failed:' + e);
        }
    };
     
## From the original plugin


## Donations

If your app is successful or if you are working for a company, please consider donating some beer money :beer::

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.me/dbaq/10)

Keep in mind that I am maintaining this repository on my free time so thank you for considering a donation. :+1:


## Contributing

I believe that everything is working, feel free to put in an issue or to fork and make pull requests if you want to add a new feature.

Things you can fix:
* Allow for null number to be passed in
  Right now, it breaks when a null value is passed in for a number, but it works if it's a blank string, and allows the user to pick the number
  It should automatically convert a  null value to an empty string

Thanks for considering contributing to this project.

### Finding something to do

Ask, or pick an issue and comment on it announcing your desire to work on it. Ideally wait until we assign it to you to minimize work duplication.

### Reporting an issue

- Search existing issues before raising a new one.

- Include as much detail as possible.

### Pull requests

- Make it clear in the issue tracker what you are working on, so that someone else doesn't duplicate the work.

- Use a feature branch, not master.

- Rebase your feature branch onto origin/master before raising the PR.

- Keep up to date with changes in master so your PR is easy to merge.

- Be descriptive in your PR message: what is it for, why is it needed, etc.

- Make sure the tests pass

- Squash related commits as much as possible.

### Coding style

- Try to match the existing indent style.

- Don't mix platform-specific stuff into the main code.




## History

-  The Android portion was forked from https://github.com/javatechig/phonegap-sms-plugin by @javatechig and then modified to upgrade it to phonegap 3.0.
- The iOS portion was copied from https://github.com/phonegap/phonegap-plugins by Jesse MacFadyen and then modified slightly to work with this plugin and phonegap 3.x by @aharris88.
- The Windows Phone 8 part was contributed by [fredrikeldh](https://github.com/fredrikeldh)
- This Android MMS extension is by @JonSchlossberg.
- This repository is now maintained by @dbaq.

## License

The MIT License (MIT)

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
