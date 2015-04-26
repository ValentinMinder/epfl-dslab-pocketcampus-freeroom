// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

#if DEBUG
using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.Food.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Food.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public MainViewModel Main { get; private set; }
        public RatingViewModel Rating { get; private set; }
        public SettingsViewModel Settings { get; private set; }

        public Design()
        {
            var foodService = new DesignFoodService();
            var meal = foodService.GetMenusAsync( null, CancellationToken.None ).Result.Menu[0].Meals[0];

            Main = new MainViewModel( new DesignDataCache(), new DesignNavigationService(), new DesignFoodService(),
                                      new DesignCredentialsStorage(), new DesignPluginSettings() );
            Rating = new RatingViewModel( new DesignFoodService(), new DesignNavigationService(), new DesignDeviceIdentifier(), meal );
            Settings = new SettingsViewModel( new DesignPluginSettings() );

            Main.OnNavigatedToAsync();
            Rating.OnNavigatedToAsync();
            Settings.OnNavigatedTo();
        }
#endif
    }
}