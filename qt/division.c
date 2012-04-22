// This code was suggested by Julian Brown from CodeSourcery. It is in public domain.
 // Many thanks!


 #if __GCCE__
// #if __SERIES60_32__
 extern unsigned int __aeabi_uidivmod(unsigned numerator, unsigned denominator);
 int __aeabi_idiv(int numerator, int denominator)
    {
    int neg_result = (numerator ^ denominator) & 0x80000000;
    int result = __aeabi_uidivmod ((numerator < 0) ? -numerator : numerator, (denominator < 0) ? -denominator : denominator);
    return neg_result ? -result : result;
    }
 unsigned __aeabi_uidiv(unsigned numerator, unsigned denominator)
    {
    return __aeabi_uidivmod (numerator, denominator);
    }
 #endif // __SERIES60_30__
 //#endif // __GCCE__


