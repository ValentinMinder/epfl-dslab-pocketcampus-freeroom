// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Collections.Generic;
using PocketCampus.Common;
using PocketCampus.News.Services;
using PocketCampus.News.ViewModels;
using ThinMvvm;

namespace PocketCampus.News
{
    public class Plugin : IPlugin
    {
        public string Id
        {
            get { return "news"; }
        }

        public bool IsVisible
        {
            get { return true; }
        }

        public bool RequiresAuthentication
        {
            get { return false; }
        }

        public void Initialize( INavigationService navigationService )
        {
            Container.Bind<INewsService, NewsService>();
        }

        public void NavigateTo( INavigationService navigationService )
        {
            navigationService.NavigateTo<MainViewModel>();
        }

        public void NavigateTo( string destination, IDictionary<string, string> parameters, INavigationService navigationService )
        {
            NavigateTo( navigationService );
        }
    }
}