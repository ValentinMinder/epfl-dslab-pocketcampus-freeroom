// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Globalization;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Camipro.Models;
using PocketCampus.Camipro.Services;
using PocketCampus.Common;
using PocketCampus.Common.Services;
using ThinMvvm;
using ThinMvvm.Logging;

namespace PocketCampus.Camipro.ViewModels
{
    /// <summary>
    /// The main (and only) ViewModel.
    /// </summary>
    [LogId( "/camipro" )]
    public sealed class MainViewModel : CachedDataViewModel<NoParameter, CamiproInfo>
    {
        private readonly ICamiproService _camiproService;
        private readonly ISecureRequestHandler _requestHandler;

        private AccountInfo _accountInfo;
        private EbankingInfo _ebankingInfo;
        private EmailSendingStatus _emailStatus;


        /// <summary>
        /// Gets the account information.
        /// </summary>
        public AccountInfo AccountInfo
        {
            get { return _accountInfo; }
            private set { SetProperty( ref _accountInfo, value ); }
        }

        /// <summary>
        /// Gets the e-banking information.
        /// </summary>
        public EbankingInfo EbankingInfo
        {
            get { return _ebankingInfo; }
            private set { SetProperty( ref _ebankingInfo, value ); }
        }

        /// <summary>
        /// Gets the status of the last sent e-mail, if any.
        /// </summary>
        public EmailSendingStatus EmailStatus
        {
            get { return _emailStatus; }
            private set { SetProperty( ref _emailStatus, value ); }
        }

        /// <summary>
        /// Gets the command executed to request an e-mail with e-banking information.
        /// </summary>
        [LogId( "RequestEmail" )]
        public AsyncCommand RequestEbankingEmailCommand
        {
            get { return GetAsyncCommand( RequestEbankingEmailAsync ); }
        }

        /// <summary>
        /// Initializes a new instance.
        /// </summary>
        public MainViewModel( ICache cache, ICamiproService camiproService, ISecureRequestHandler requestHandler )
            : base( cache )
        {
            _camiproService = camiproService;
            _requestHandler = requestHandler;
        }

        /// <summary>
        /// Requests an e-mail with e-banking information.
        /// </summary>
        private async Task RequestEbankingEmailAsync()
        {
            EmailStatus = await _requestHandler.ExecuteAsync<MainViewModel, TequilaToken, CamiproSession, EmailSendingStatus>( _camiproService, async session =>
            {
                var request = new CamiproRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    Session = new SessionId { CamiproCookie = session.Cookie }
                };

                try
                {
                    var result = await _camiproService.RequestEBankingEMailAsync( request );
                    return result.Status == ResponseStatus.Success ? EmailSendingStatus.Success : EmailSendingStatus.Error;
                }
                catch
                {
                    return EmailSendingStatus.Error;
                }
            } );
        }

        /// <summary>
        /// Gets data from the server.
        /// </summary>
        protected override CachedTask<CamiproInfo> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<CamiproInfo>();
            }

            return CachedTask.Create( () => _requestHandler.ExecuteAsync<MainViewModel, TequilaToken, CamiproSession, CamiproInfo>( _camiproService, async session =>
            {
                var request = new CamiproRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    Session = new SessionId { CamiproCookie = session.Cookie }
                };

                var accountTask = _camiproService.GetAccountInfoAsync( request, token );
                var ebankingTask = _camiproService.GetEBankingInfoAsync( request, token );

                await Task.WhenAll( accountTask, ebankingTask );

                return new CamiproInfo( accountTask.Result, ebankingTask.Result );
            } ) );
        }

        /// <summary>
        /// Handles data received from the server.
        /// </summary>
        protected override bool HandleData( CamiproInfo data, CancellationToken token )
        {
            if ( data.AccountInfo.Status == ResponseStatus.NetworkError || data.EbankingInfo.Status == ResponseStatus.NetworkError )
            {
                throw new Exception( "Server error while getting the account or e-banking info." );
            }
            if ( data.AccountInfo.Status == ResponseStatus.AuthenticationError || data.EbankingInfo.Status == ResponseStatus.AuthenticationError )
            {
                _requestHandler.Authenticate<MainViewModel>();
                return false;
            }

            if ( !token.IsCancellationRequested )
            {
                AccountInfo = data.AccountInfo;
                EbankingInfo = data.EbankingInfo;
            }

            return true;
        }
    }
}