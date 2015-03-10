// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Camipro.Models;
using PocketCampus.Common.Services;
using ThriftSharp;

namespace PocketCampus.Camipro.Services
{
    [ThriftService( "CamiproService" )]
    public interface ICamiproService : ITwoStepAuthenticator<TequilaToken, CamiproSession>
    {
        [ThriftMethod( "getTequilaTokenForCamipro" )]
        new Task<TequilaToken> GetTokenAsync();

        [ThriftMethod( "getCamiproSession" )]
        new Task<CamiproSession> GetSessionAsync( [ThriftParameter( 1, "iTequilaToken" )] TequilaToken token );


        [ThriftMethod( "getBalanceAndTransactions" )]
        Task<AccountInfo> GetAccountInfoAsync( [ThriftParameter( 1, "iRequest" )] CamiproRequest request, CancellationToken token );

        [ThriftMethod( "getStatsAndLoadingInfo" )]
        Task<EbankingInfo> GetEBankingInfoAsync( [ThriftParameter( 1, "iRequest" )] CamiproRequest request, CancellationToken token );


        // The e-mail is sent to the student's @epfl.ch address.
        [ThriftMethod( "sendLoadingInfoByEmail" )]
        Task<MailRequestResult> RequestEBankingEMailAsync( [ThriftParameter( 1, "iRequest" )] CamiproRequest request );
    }
}