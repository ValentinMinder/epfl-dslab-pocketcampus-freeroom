// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Authentication;
using PocketCampus.Camipro.Models;
using PocketCampus.Camipro.Services;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Camipro.ViewModels
{
    [LogId( "/camipro" )]
    public sealed class MainViewModel : DataViewModel<NoParameter>
    {
        private readonly INavigationService _navigationService;
        private readonly ISecureRequestHandler _requestHandler;
        private readonly ICamiproService _camiproService;


        private AccountInfo _accountInfo;
        private EbankingInfo _ebankingInfo;
        private EmailSendingStatus _emailStatus;


        public AccountInfo AccountInfo
        {
            get { return _accountInfo; }
            private set { SetProperty( ref _accountInfo, value ); }
        }

        public EbankingInfo EbankingInfo
        {
            get { return _ebankingInfo; }
            private set { SetProperty( ref _ebankingInfo, value ); }
        }

        public EmailSendingStatus EmailStatus
        {
            get { return _emailStatus; }
            private set { SetProperty( ref _emailStatus, value ); }
        }


        [LogId( "RequestEmail" )]
        public AsyncCommand RequestEbankingEmailCommand
        {
            get { return this.GetAsyncCommand( RequestEbankingEmailAsync ); }
        }


        public MainViewModel( INavigationService navigationService, ISecureRequestHandler requestHandler, ICamiproService camiproService )
        {
            _navigationService = navigationService;
            _requestHandler = requestHandler;
            _camiproService = camiproService;
        }


        protected override async Task RefreshAsync( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return;
            }

            var accountAndEbanking = await _requestHandler.ExecuteAsync( async () =>
            {
                var request = new CamiproRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                };

                var accountTask = _camiproService.GetAccountInfoAsync( request, token );
                var ebankingTask = _camiproService.GetEBankingInfoAsync( request, token );

                // parallel requests
                var accountInfo = await accountTask;
                var ebankingInfo = await ebankingTask;

                if ( accountInfo.Status == ResponseStatus.NetworkError || ebankingInfo.Status == ResponseStatus.NetworkError )
                {
                    throw new Exception( "Server error while getting the account or e-banking info." );
                }
                if ( accountInfo.Status == ResponseStatus.AuthenticationError || ebankingInfo.Status == ResponseStatus.AuthenticationError )
                {
                    _navigationService.ForceAuthentication<MainViewModel>();
                    return null;
                }

                return Tuple.Create( accountInfo, ebankingInfo );
            } );

            if ( accountAndEbanking != null && !token.IsCancellationRequested )
            {
                AccountInfo = accountAndEbanking.Item1;
                EbankingInfo = accountAndEbanking.Item2;
            }
        }


        private async Task RequestEbankingEmailAsync()
        {
            EmailStatus = EmailSendingStatus.Requested;

            try
            {
                EmailStatus = await _requestHandler.ExecuteAsync( async () =>
                {
                    var request = new CamiproRequest
                    {
                        Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName
                    };

                    var result = await _camiproService.RequestEBankingEMailAsync( request );
                    return result.Status == ResponseStatus.Success ? EmailSendingStatus.Success : EmailSendingStatus.Error;
                } );
            }
            catch
            {
                EmailStatus = EmailSendingStatus.Error;
            }
        }
    }
}