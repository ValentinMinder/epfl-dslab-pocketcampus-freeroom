// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Camipro.Models;
using PocketCampus.Common.Services;
using ThriftSharp;

namespace PocketCampus.Camipro.Services
{
    /// <summary>
    /// The CAMIPRO server service.
    /// </summary>
    [ThriftService( "CamiproService" )]
    public interface ICamiproService : ITwoStepAuthenticator<TequilaToken, CamiproSession>
    {
        /// <summary>
        /// First authentication step: asynchronously gets a token.
        /// </summary>
        [ThriftMethod( "getTequilaTokenForCamipro" )]
        new Task<TequilaToken> GetTokenAsync();

        /// <summary>
        /// Second authentication step: asynchronously gets a session from an authenticated token.
        /// </summary>
        [ThriftMethod( "getCamiproSession" )]
        new Task<CamiproSession> GetSessionAsync( [ThriftParameter( 1, "iTequilaToken" )] TequilaToken token );


        /// <summary>
        /// Asynchronously gets information about a CAMIPRO card.
        /// </summary>
        [ThriftMethod( "getBalanceAndTransactions" )]
        Task<AccountInfo> GetAccountInfoAsync( [ThriftParameter( 1, "iRequest" )] CamiproRequest request, CancellationToken token );

        /// <summary>
        /// Asynchronously gets information about the e-banking payment used to charge a CAMIPRO card.
        /// </summary>
        [ThriftMethod( "getStatsAndLoadingInfo" )]
        Task<EbankingInfo> GetEBankingInfoAsync( [ThriftParameter( 1, "iRequest" )] CamiproRequest request, CancellationToken token );

        /// <summary>
        /// Asynchronously requests an e-mail containing information about the e-banking payment used to charge a CAMIPRO card.
        /// </summary>
        /// <remarks>
        /// The e-mail will be sent to the student's @epfl.ch e-mail address.
        /// </remarks>
        [ThriftMethod( "sendLoadingInfoByEmail" )]
        Task<MailRequestResult> RequestEBankingEMailAsync( [ThriftParameter( 1, "iRequest" )] CamiproRequest request );
    }
}