phantom.injectJs("jquery.js");  // include jquery 
var page = require('webpage').create(),
    system = require('system');
address = system.args[1];
fileName  = system.args[2];

page.paperSize = {
format:"A4",
orientation:"portrait",
margin:{left:"0.5cm",right:"0.5cm",top:"1cm",bottom:"1cm"}
};


page.open(address, function(status) {
if(status !== 'success'){
console.log("Due to some issue unable to load the address. Please check the address again");
phantom.exit();
}
else{	


page.render(fileName);
    console.log("PDF GENERATED");
    phantom.exit();
}
});
