// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Camipro.Models;
using PocketCampus.Common.Services;
using ThriftSharp;

// Plumbing for ICamiproService.

namespace PocketCampus.Camipro.Services
{
    public sealed class CamiproService : ThriftServiceImplementation<ICamiproService>, ICamiproService
    {
        public CamiproService( IServerAccess config ) : base( config.CreateCommunication( "camipro" ) ) { }

        public Task<AccountInfo> GetAccountInfoAsync( CamiproRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<CamiproRequest, CancellationToken, AccountInfo>( x => x.GetAccountInfoAsync, request, cancellationToken );
        }

        public Task<EbankingInfo> GetEBankingInfoAsync( CamiproRequest request, CancellationToken cancellationToken )
        {
            return CallAsync<CamiproRequest, CancellationToken, EbankingInfo>( x => x.GetEBankingInfoAsync, request, cancellationToken );
        }

        public Task<MailRequestResult> RequestEBankingEMailAsync( CamiproRequest request )
        {
            return CallAsync<CamiproRequest, MailRequestResult>( x => x.RequestEBankingEMailAsync, request );
        }
    }
}