// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using PocketCampus.Camipro.Models;
using PocketCampus.Common;

// Design data for MainViewModel.

namespace PocketCampus.Camipro.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }

        public AccountInfo AccountInfo
        {
            get
            {
                return new AccountInfo
                {
                    Balance = 84.30,
                    Transactions = new[]
                    {
                        new Transaction
                        {
                            Amount = 120.00,
                            Operation = "Chargement badge facture",
                            Place = "Chargement e-banking",
                            Date = new DateTime(2013, 5, 15, 11, 36, 26)
                        },
                        new Transaction
                        {
                            Amount = -2.5,
                            Operation = "Vente",
                            Place = "COMPASS_SG_SANTAFE (334295)",
                            Date = new DateTime(2013, 5, 14, 17, 00, 15)
                        },
                        new Transaction
                        {
                            Amount = -3.0,
                            Operation = "Vente",
                            Place = "Compass Giacometti",
                            Date = new DateTime(2013, 5, 15, 15, 13, 01)
                        },
                        new Transaction
                        {
                            Amount = -7.00,
                            Operation = "Vente",
                            Place = "Compass Hodler 2",
                            Date = new DateTime(2013, 5, 14, 11, 41, 34)
                        },
                        new Transaction
                        {
                            Amount = -1.80,
                            Operation = "Vente",
                            Place = "Le Négoce",
                            Date = new DateTime(2013, 5, 13, 12, 14, 26)
                        },
                        new Transaction
                        {
                            Amount = -7.00,
                            Operation = "Vente",
                            Place = "Compass Hodler 1",
                            Date = new DateTime(2013, 5, 13, 11, 32, 14)
                        },
                        new Transaction
                        {
                            Amount = -1.90,
                            Operation = "Vente",
                            Place = "Esplanade 2 * G. Riffault",
                            Date = new DateTime(2013, 5, 8, 11, 52, 17)
                        },
                        new Transaction
                        {
                            Amount = -7.00,
                            Operation = "Vente",
                            Place = "Compass Hodler 2",
                            Date = new DateTime(2013, 5, 8, 11, 33, 00)
                        },
                        new Transaction
                        {
                            Amount = -1.80,
                            Operation = "Vente",
                            Place = "SELECTA_BS_STTROPEZ (326290)",
                            Date = new DateTime(2013, 5, 8, 9, 56, 32)
                        },
                        new Transaction
                        {
                            Amount = -3.70,
                            Operation = "Vente",
                            Place = "Le Négoce",
                            Date = new DateTime(2013, 5, 8, 12, 22, 48)
                        },
                    },
                    Status = ResponseStatus.Success
                };
            }
        }

        public EbankingInfo EbankingInfo
        {
            get
            {
                return new EbankingInfo
                {
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
                    },
                    Status = ResponseStatus.Success
                };
            }
        }

        public EmailSendingStatus EmailStatus { get { return EmailSendingStatus.Success; } }

#endif
    }
}