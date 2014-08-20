// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Camipro.Models
{
    /// <summary>
    /// Information about a CAMIPRO account balance and e-banking.
    /// </summary>
    /// <remarks>
    /// This class is used to cache both an <see cref="AccountInfo" /> and an <see cref="EbankingInfo" />, 
    /// <see cref="Tuple{T1,T2}" /> can't be used because it's not serializable by DataContractSerializers.
    /// </remarks>
    public sealed class CamiproInfo
    {
        /// <summary>
        /// The account info.
        /// </summary>
        public AccountInfo AccountInfo { get; set; }

        /// <summary>
        /// The e-banking info.
        /// </summary>
        public EbankingInfo EbankingInfo { get; set; }


        /// <summary>
        /// Initializes a new instance for serialization purposes.
        /// </summary>
        public CamiproInfo() { }

        /// <summary>
        /// Initializes a new instance of the <see cref="CamiproInfo" /> class with the specified account and e-banking info.
        /// </summary>
        public CamiproInfo( AccountInfo accountInfo, EbankingInfo ebankingInfo )
        {
            AccountInfo = accountInfo;
            EbankingInfo = ebankingInfo;
        }
    }
}