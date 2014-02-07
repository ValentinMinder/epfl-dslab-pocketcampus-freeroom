// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
using PocketCampus.Camipro.Models;
using PocketCampus.Common.Services;
using ThriftSharp;

// Plumbing for ICamiproService.

namespace PocketCampus.Camipro.Services
{
    public sealed class CamiproService : ThriftServiceImplementation<ICamiproService>, ICamiproService
    {
        public CamiproService( IServerConfiguration config )
            : base( config.CreateCommunication( "camipro" ) )
        {
        }

        public Task<TequilaToken> GetTokenAsync()
        {
            return CallAsync<TequilaToken>( x => x.GetTokenAsync );
        }

        public Task<CamiproSession> GetSessionAsync( TequilaToken token )
        {
            return CallAsync<TequilaToken, CamiproSession>( x => x.GetSessionAsync, token );
        }

        public Task<AccountInfo> GetAccountInfoAsync( CamiproRequest request )
        {
            return CallAsync<CamiproRequest, AccountInfo>( x => x.GetAccountInfoAsync, request );
        }

        public Task<EbankingInfo> GetEBankingInfoAsync( CamiproRequest request )
        {
            return CallAsync<CamiproRequest, EbankingInfo>( x => x.GetEBankingInfoAsync, request );
        }

        public Task<MailRequestResult> RequestEBankingEMailAsync( CamiproRequest request )
        {
            return CallAsync<CamiproRequest, MailRequestResult>( x => x.RequestEBankingEMailAsync, request );
        }
    }
}