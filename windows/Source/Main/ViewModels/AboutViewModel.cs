// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Reflection;
using PocketCampus.Common.Services;
using PocketCampus.Main.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Main.ViewModels
{
    [LogId( "/dashboard/about" )]
    public sealed class AboutViewModel : ViewModel<NoParameter>
    {
        private readonly IBrowserService _browserService;
        private readonly IEmailService _emailService;
        private readonly IAppRatingService _ratingService;


        public string AppVersion
        {
            get { return typeof( AboutViewModel ).GetTypeInfo().Assembly.GetName().Version.ToString( 3 ); }
        }


        [LogId( "ViewWebsite" )]
        public Command<string> BrowseUrlCommand
        {
            get { return this.GetCommand<string>( _browserService.NavigateTo ); }
        }

        [LogId( "SendEmail" )]
        public Command<string> SendEmailCommand
        {
            get { return this.GetCommand<string>( _emailService.ComposeEmail ); }
        }

        [LogId( "RateOnStore" )]
        public Command RateAppCommand
        {
            get { return this.GetCommand( _ratingService.RequestRating ); }
        }


        public AboutViewModel( IBrowserService browserService, IEmailService emailService, IAppRatingService ratingService )
        {
            _browserService = browserService;
            _emailService = emailService;
            _ratingService = ratingService;
        }
    }
}