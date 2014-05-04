// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Camipro.Models
{
    /// <summary>
    /// Information about a CAMIPRO account balance and e-banking.
    /// </summary>
    /// <remarks>
    /// This class is used to cache both an AccountInfo and an EbankingInfo, 
    /// Tuple can't be used because it's not serializable by DataContractSerializers.
    /// </remarks>
    public sealed class CamiproInfo
    {
        /// <summary>
        /// Gets or sets the account info.
        /// </summary>
        public AccountInfo AccountInfo { get; set; }

        /// <summary>
        /// Gets or sets the e-banking info.
        /// </summary>
        public EbankingInfo EbankingInfo { get; set; }


        /// <summary>
        /// Creates an empty CamiproInfo, for serialization purposes.
        /// </summary>
        public CamiproInfo() { }

        /// <summary>
        /// Creates a CamiproInfo with the specified account and e-banking info.
        /// </summary>
        public CamiproInfo( AccountInfo accountInfo, EbankingInfo ebankingInfo )
        {
            AccountInfo = accountInfo;
            EbankingInfo = ebankingInfo;
        }
    }
}