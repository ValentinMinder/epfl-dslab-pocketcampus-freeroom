// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for ICamiproService

#if DEBUG
using System;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Camipro.Models;

namespace PocketCampus.Camipro.Services.Design
{
    public sealed class DesignCamiproService : ICamiproService
    {
        public Task<TequilaToken> GetTokenAsync()
        {
            return Task.FromResult( new TequilaToken() );
        }

        public Task<CamiproSession> GetSessionAsync( TequilaToken token )
        {
            return Task.FromResult( new CamiproSession() );
        }

        public Task<AccountInfo> GetAccountInfoAsync( CamiproRequest request, CancellationToken token )
        {
            return Task.FromResult
            (
                new AccountInfo
                {
                    Status = ResponseStatus.Success,
                    Balance = 84.30,
                    Transactions = new[]
                    {
                        new Transaction
                        {
                            Amount = 120.00,
                            Place = "Chargement e-banking",
                            Date = new DateTime(2013, 5, 15, 11, 36, 26)
                        },
                        new Transaction
                        {
                            Amount = -2.5,
                            Place = "COMPASS_SG_SANTAFE (334295)",
                            Date = new DateTime(2013, 5, 14, 17, 00, 15)
                        },
                        new Transaction
                        {
                            Amount = -3.0,
                            Place = "Compass Giacometti",
                            Date = new DateTime(2013, 5, 15, 15, 13, 01)
                        },
                        new Transaction
                        {
                            Amount = -7.00,
                            Place = "Compass Hodler 2",
                            Date = new DateTime(2013, 5, 14, 11, 41, 34)
                        },
                        new Transaction
                        {
                            Amount = -1.80,
                            Place = "Le Négoce",
                            Date = new DateTime(2013, 5, 13, 12, 14, 26)
                        },
                        new Transaction
                        {
                            Amount = -7.00,
                            Place = "Compass Hodler 1",
                            Date = new DateTime(2013, 5, 13, 11, 32, 14)
                        },
                        new Transaction
                        {
                            Amount = -1.90,
                            Place = "Esplanade 2 * G. Riffault",
                            Date = new DateTime(2013, 5, 8, 11, 52, 17)
                        },
                        new Transaction
                        {
                            Amount = -7.00,
                            Place = "Compass Hodler 2",
                            Date = new DateTime(2013, 5, 8, 11, 33, 00)
                        },
                        new Transaction
                        {
                            Amount = -1.80,
                            Place = "SELECTA_BS_STTROPEZ (326290)",
                            Date = new DateTime(2013, 5, 8, 9, 56, 32)
                        },
                        new Transaction
                        {
                            Amount = -3.70,
                            Place = "Le Négoce",
                            Date = new DateTime(2013, 5, 8, 12, 22, 48)
                        }
                    }
                }
            );
        }

        public Task<EbankingInfo> GetEBankingInfoAsync( CamiproRequest request, CancellationToken token )
        {
            return Task.FromResult
            (
                new EbankingInfo
                {
                    Status = ResponseStatus.Success,
                    PaymentInfo = new PaymentInfo
                    {
                        AccountName = "Ecole Polytechnique Fédérale de Lausanne\nDébiteurs\n1015 Lausanne",
                        AccountNumber = "01-23456-7",
                        ReferenceNumber = "00 00000 01234 12345 12345 12345"
                    },
                    CardStatistics = new CardStatistics
                    {
                        MonthTotal = 186.50,
                        ThreeMonthsTotal = 420.60
                    }
                }
            );
        }

        public Task<MailRequestResult> RequestEBankingEMailAsync( CamiproRequest request )
        {
            return Task.FromResult
            (
                new MailRequestResult
                {
                    Status = ResponseStatus.Success
                }
            );
        }
    }
}
#endif