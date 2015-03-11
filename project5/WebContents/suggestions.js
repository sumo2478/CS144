/**
 * Provides suggestions for state names (USA).
 * @class
 * @scope public
 */
function StateSuggestions(suggestionHandler) {

}

/**
 * Request suggestions for the given autosuggest control. 
 * @scope protected
 * @param oAutoSuggestControl The autosuggest control to provide suggestions for.
 */
StateSuggestions.prototype.requestSuggestions = function (oAutoSuggestControl /*:AutoSuggestControl*/,
                                                          bTypeAhead /*:boolean*/) {
    var textboxValue = encodeURIComponent(oAutoSuggestControl.textbox.value);
    var baseUrl = "/eBay/suggest?q=";
    var urlToSearch = baseUrl + textboxValue;

    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
        var suggestions = [];
        if (xmlhttp.readyState == 4) {
            var xmlSuggestions = xmlhttp.responseXML.documentElement.getElementsByTagName("suggestion");              

            for (var i = 0; i < xmlSuggestions.length; i++) {
                var item = xmlSuggestions[i].getAttribute("data");                
                suggestions.push(item);
            };             

            //provide suggestions to the control
            oAutoSuggestControl.autosuggest(suggestions, false);
        };        
    }
    
    xmlhttp.open("GET",urlToSearch,true);
    xmlhttp.send();    
};