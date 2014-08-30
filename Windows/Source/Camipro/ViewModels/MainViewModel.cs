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
    public sealed class MainViewModel : DataViewModel<NoParameter>
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
            get { return this.GetAsyncCommand( RequestEbankingEmailAsync ); }
        }

        /// <summary>
        /// Initializes a new instance.
        /// </summary>
        public MainViewModel( ICamiproService camiproService, ISecureRequestHandler requestHandler )
        {
            _camiproService = camiproService;
            _requestHandler = requestHandler;
        }

        /// <summary>
        /// Requests an e-mail with e-banking information.
        /// </summary>
        private Task RequestEbankingEmailAsync()
        {
            return _requestHandler.ExecuteAsync<MainViewModel, TequilaToken, CamiproSession>( _camiproService, async session =>
            {
                var request = new CamiproRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    Session = new SessionId { CamiproCookie = session.Cookie }
                };

                try
                {
                    var result = await _camiproService.RequestEBankingEMailAsync( request );
                    EmailStatus = result.Status == ResponseStatus.Success ? EmailSendingStatus.Success : EmailSendingStatus.Error;
                }
                catch
                {
                    EmailStatus = EmailSendingStatus.Error;
                }
            } );
        }

        /// <summary>
        /// Asynchronously refreshes the data.
        /// </summary>
        protected override Task RefreshAsync( bool force, CancellationToken token )
        {
            return _requestHandler.ExecuteAsync<MainViewModel, TequilaToken, CamiproSession>( _camiproService, async session =>
            {
                if ( !force )
                {
                    return;
                }

                var request = new CamiproRequest
                {
                    Language = CultureInfo.CurrentUICulture.TwoLetterISOLanguageName,
                    // HACK to make design data work :(
                    Session = new SessionId { CamiproCookie = session == null ? null : session.Cookie }
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
                    _requestHandler.Authenticate<MainViewModel>();
                    return;
                }

                if ( !token.IsCancellationRequested )
                {
                    AccountInfo = accountInfo;
                    EbankingInfo = ebankingInfo;
                }
            } );
        }
    }
}