
/*-----------------------------------------------------------------------------------*/
/*  Start Custom jQuery
/*-----------------------------------------------------------------------------------*/

$(document).ready(function(){
    
/*-----------------------------------------------------------------------------------*/
/*  Configure Slides and Feature List
/*-----------------------------------------------------------------------------------*/

    $('ul.features li.top-row').equalize('height');
    
    $('#slides-android').cycle({fx:'fadeout', speed:1000, timeout: 50});
    
/*-----------------------------------------------------------------------------------*/
/*  Tab & Panel Switches
/*-----------------------------------------------------------------------------------*/
    
    $('#panel_iphone_tab').click(function(){
        $('#ipad').hide();
        $('#android').hide();
        $('#windowsphone').hide();
        $('#iphone').show();
        
        $('#header_support').hide(); // see iPad tab click for explanation
        
        $('#slides-iphone').cycle('destroy');
        $('#slides-ipad').cycle('destroy');
        $('#slides-android').cycle('destroy');
        $('#slides-windowsphone').cycle('destroy');
        
        $('#slides-iphone').cycle({fx:'fadeout', speed:800, timeout: 3000});
    });
    
    $('#panel_ipad_tab').click(function(){
        $('#iphone').hide();
        $('#android').hide();
        $('#windowsphone').hide();
        $('#ipad').show();
        
        $('#header_support').show(); // iPad is large => prevent bug of header no repeating on iPad device (Safari browser)
        
        $('#slides-iphone').cycle('destroy');
        $('#slides-ipad').cycle('destroy');
        $('#slides-android').cycle('destroy');
        $('#slides-windowsphone').cycle('destroy');
        
        $('#slides-ipad').cycle({fx:'fadeout', speed:800, timeout: 3000});
    });
    
    $('#panel_android_tab').click(function(){
        $('#iphone').hide();
        $('#ipad').hide();
        $('#windowsphone').hide();
        $('#android').show();
        
        $('#header_support').hide(); // see iPad tab click for explanation
        
        $('#slides-iphone').cycle('destroy');
        $('#slides-ipad').cycle('destroy');
        $('#slides-android').cycle('destroy');
        $('#slides-windowsphone').cycle('destroy');
        
        $('#slides-android').cycle({fx:'fadeout', speed:800, timeout: 3000});
    });
    
    $('#panel_windowsphone_tab').click(function(){
        $('#iphone').hide();
        $('#ipad').hide();
        $('#android').hide();
        $('#windowsphone').show();
        
        $('#header_support').hide(); // see iPad tab click for explanation
        
        $('#slides-iphone').cycle('destroy');
        $('#slides-ipad').cycle('destroy');
        $('#slides-android').cycle('destroy');
        $('#slides-windowsphone').cycle('destroy');
        
        $('#slides-windowsphone').cycle({fx:'fadeout', speed:800, timeout: 3000});
    });
    
    var current = '#' + $('#tabs li.current').attr('data-panel');
    
    $('#tabs li').not(".disabled").click(function(){
        // Get current and clicked panels
        
        var clicked = '#' + $(this).attr('data-panel');
        
        if(isFrench === true) {
            clicked += '_fr';
        }
        
        console.log('current: ' + current);
        console.log('clicked: ' + clicked);
                
        /*if(current === clicked) {
            return;
        }*/
        
        $('#tabs li').removeClass('current');
        $(this).addClass('current');
                
        $(current).hide();
        $(clicked).show();
        
        current = clicked;
    });
    
/*-----------------------------------------------------------------------------------*/
/*  Switches to tab best suited for OS
/*-----------------------------------------------------------------------------------*/

    /* Normal behavior */
    /*if($.client.os == "Mac" || $.client.browser == "Safari") {
        $('#panel_iphone_tab').trigger('click');
    }*/

    
    /* iPad showoff behavior */
    if (/Android/i.test(navigator.userAgent)) {
        $('#panel_android_tab').trigger('click');
    } else if (/iPad/i.test(navigator.userAgent)) {
        $('#panel_ipad_tab').trigger('click');
    } else {
        $('#panel_iphone_tab').trigger('click');
    }
    
    var language = window.navigator.userLanguage || window.navigator.language;
    var isFrench = (language.indexOf('fr')!=-1);
    
    $('#language-switch').click(function(){
        isFrench = !isFrench;
        console.log('french? '+isFrench);
        updateLanguageButton();
        updateSloganLanguage();
        updateInstallSteps();
    });
    
    updateLanguageButton();
    
    updateSloganLanguage();

    updateInstallSteps();

    function updateLanguageButton() {
        var switchButton = $("#language-switch");
        //console.log(switchButton);
    
        if(isFrench) {
            switchButton.text("English");
        } else {
            switchButton.text("Français");
        }
        
        $('#tabs li.current').trigger('click');
    }
    
    function updateSloganLanguage() {
        if (isFrench) {
            $("#slogan_en").hide();
            $("#slogan_fr").show();
        } else {
            $("#slogan_en").show();
            $("#slogan_fr").hide();
        }
    }

    function updateInstallSteps(){
        if (isFrench) {
            $("#install_en").hide();
            $("#install_fr").show();
        } else {
            $("#install_en").show();
            $("#install_fr").hide();
        }
    }

});

















