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

        private CamiproRequest _lastRequest;

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
        /// Creates a new MainViewModel.
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
            bool threw = false;
            try
            {
                var result = await _camiproService.RequestEBankingEMailAsync( _lastRequest );
                if ( result.Status != ResponseStatus.Success )
                {
                    throw new Exception( "Server error while requesting an e-banking e-mail." );
                }
            }
            catch // catches both the manually thrown exception as well as the ones thrown by RequestEBankingEMailAsync
            {
                EmailStatus = EmailSendingStatus.Error;
                threw = true;
            }

            if ( !threw )
            {
                EmailStatus = EmailSendingStatus.Success;
            }
        }

        protected override CachedTask<CamiproInfo> GetData( bool force, CancellationToken token )
        {
            if ( !force )
            {
                return CachedTask.NoNewData<CamiproInfo>();
            }

            return CachedTask.Create( () => _requestHandler.ExecuteAsync<MainViewModel, TequilaToken, CamiproSession, CamiproInfo>( _camiproService, async session =>
            {
                _lastRequest = new CamiproRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    Session = new SessionId { CamiproCookie = session.Cookie }
                };

                return new CamiproInfo( await _camiproService.GetAccountInfoAsync( _lastRequest, token ),
                                        await _camiproService.GetEBankingInfoAsync( _lastRequest, token ) );
            } ) );
        }

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