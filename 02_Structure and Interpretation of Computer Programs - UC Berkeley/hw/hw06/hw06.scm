; ;;;;;;;;;;;;;;
; ; Questions ;;
; ;;;;;;;;;;;;;;
; Scheme
(define (cddr s) (cdr (cdr s)))

(define (cadr s) (car (cdr s)))

(define (caddr s) (car (cdr (cdr s))))

(define (sign x)
  (cond 
    ((< x 0) -1)
    ((= x 0) 0)
    ((> x 0) 1)))

(define (square x) (* x x))

(define (pow b n)
  (cond 
    ((= n 0)   1)
    ((= n 1)   b)
    ((even? n) (* (square b) (pow b (/ n 2))))
    ((odd? n)  (pow b (- n 1)))))

(define (unique s)
  (define a_in_list
          (lambda (a) (not (eq? a (car s)))))
  (cond 
    ((null? s)
     nil)
    (else
     (cons (car s)
           (unique (filter a_in_list (cdr s)))))))
