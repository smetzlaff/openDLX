/* printf.c
 * Small printf() implementation.
 * Implements only %%, %c, %s, %[+, ,0][width][l,ll][d,o,u,x]
 *
 * Copyright (c) 2010 Jörg Mische <bobbl@gmx.de>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

#include "printf.h"

#if (MIPSEL==0) || (MIPSSIM==0)
#error "Code works only on MIPSEL mipsSim."
#endif

// called to print one character
static void put(char c)
{
	asm volatile ("addi $k0,$zero,%0\n\t add $k1,$zero,%1\n\t SYSCALL " : : "i" (PRINT_CHAR), "g" ((int)c));
}

static int gprintf(char *dest, size_t  maxlen, const char *format, va_list ap)
{
    int len = 0;
    int c;

    while (1) 
    {
	c = *format++;
	if (c == 0)
	{
	    if (dest!=0 && maxlen>0)
		dest[maxlen-1] = 0;
	    va_end(ap);
	    return len;
	}
	if (c == '%')
	{
	    c = *format++;
	    if (c == 'c')
	    {
		c = va_arg(ap, int);
	    }
	    else if (c == 's')
	    {
	        char *s = va_arg(ap, char *);
		while (*s)
		{
		    if (dest==0)
			put(*s++); 
		    else if (len<maxlen)
			dest[len] = *s++;
		    len++;
		}
		continue;
	    }
	    else if (c != '%')
	    {
		// print number
		int sign = 0;
		if (c == '+' || c == ' ')
		{
		    sign = c;
		    c = *format++;
		}
	
		int zero = ' ';
		if (c == '0') 
		{
		    zero = '0';
		    c = *format++;
		}
		// shorter: int zero = (c=='0') ? (c=*format++, '0') : ' '; 
	
		int width = 0;
		while (c >= '0' && c <= '9')
		{
		    width = 10*width + (c - '0');
		    c = *format++;
		}

		int l = 0;
		while (c == 'l') 
		{
		    l++;
		    c = *format++;
		}

		unsigned long long x;
		unsigned radix = 10;
		if (c == 'd')
		{
	    	    if (l == 0)
			x = va_arg(ap, int);
		    else if (l == 1)
			x = va_arg(ap, long);
		    else
			x = va_arg(ap, long long);
	
		    if ((signed long long)x < 0)
		    {
			if (zero == '0')
			{
			    // special case: avoid "000-1"
			    if (dest==0)
    	    	    		put('-'); 
			    else if (len<maxlen)
				dest[len] = '-';
			    len++;
			    width--;
			    sign = 0;
			}
			else
			    sign = '-';
			x = -x;
		    }
		}
		else
		{
		    if (c == 'x')
			radix = 16;
		    else if (c == 'o')
			radix = 8;
		    else if (c != 'u')
			continue; // unknown command
	
		    if (l == 0)
			x = va_arg(ap, unsigned int);
		    else if (l == 1)
		    	x = va_arg(ap, unsigned long);
		    else
			x = va_arg(ap, unsigned long long);
		}
	
		char buf[22];
		int i = 0;
		do
		{
		    unsigned dig = x % radix;
		    x /= radix;
		    buf[i++] = dig + (dig >= 10 ? 'a' - 10 : '0');
		}
		while (x != 0);
		
	    	if (sign != 0)
		    buf[i++] = sign;
	
		while (width > i) 
		{
		    if (dest==0)
			put(zero); 
		    else if (len<maxlen)
			dest[len] = zero;
		    len++;
		    width--;
		}
			
		while (i>0)
		{
		    if (dest==0)
			put(buf[--i]); 
		    else if (len<maxlen)
			dest[len] = buf[--i];
		    len++;
		}
		continue;
	    }
	}
	if (dest==0)
	    put(c); 
	else if (len<maxlen)
	    dest[len] = c;
	len++;
    }
}


//int putchar(int c)
//{
//	put(c);
//	return c;
//}

int printf(const char *format, ...)
{
    va_list ap;
    va_start(ap, format);
    return gprintf(0, 0, format, ap);
}

int snprintf(char *str, size_t size, const char *format, ...)
{
    va_list ap;
    va_start(ap, format);
    if (size==0) str = (void *)&ap; // avoid null pointer which indicates writing to stdout
    return gprintf(str, size, format, ap);
}

